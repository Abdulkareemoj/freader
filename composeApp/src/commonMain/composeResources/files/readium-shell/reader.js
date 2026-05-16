/**
 * Freader Readium Navigator Bridge
 * This handles the interaction between the WebView and the Readium CSS / Navigator logic.
 */

window.freader = {
    currentManifest: null,
    currentBaseUrl: '',
    currentIndex: 0,
    settings: {
        appearance: 'readium-default-on',
        fontSize: '100%',
        fontFamily: 'serif',
        columnCount: '1',
        lineHeight: '1.5',
        pageMargins: '1.0'
    },

    init: function(baseUrl, initialLocator, format, manifestJson) {
        console.log("Initializing Readium Navigator...");
        this.currentBaseUrl = baseUrl;
        this.currentManifest = JSON.parse(manifestJson);

        let locator = null;
        try {
            if (initialLocator && initialLocator !== "null") {
                locator = JSON.parse(initialLocator);
            }
        } catch (e) {
            console.error("Failed to parse initial locator:", e);
        }

        if (format === 'EPUB') {
            this.loadEpub(locator);
        } else {
            console.error("Unsupported format for navigator:", format);
        }

        window.addEventListener('resize', () => {
            this.reportProgress();
        });
    },

    loadEpub: function(initialLocator) {
        const spine = this.currentManifest.readingOrder;
        if (!spine || spine.length === 0) return;

        const container = document.getElementById('reader-container');
        container.innerHTML = '';

        const iframe = document.createElement('iframe');
        iframe.id = 'readium-iframe';
        iframe.name = 'reader-iframe';
        iframe.style.width = '100%';
        iframe.style.height = '100%';
        iframe.style.border = 'none';
        iframe.style.background = 'white';

        container.appendChild(iframe);

        let targetHref = spine[0].href;
        let progression = 0;

        if (initialLocator && initialLocator.href) {
            targetHref = initialLocator.href;
            progression = initialLocator.locations?.progression || 0;

            // Find current index in spine
            this.currentIndex = spine.findIndex(item => item.href === targetHref);
            if (this.currentIndex === -1) this.currentIndex = 0;
        }

        iframe.onload = () => {
            this.injectAssets(iframe.contentDocument);
            this.applySettings(iframe.contentDocument);
            this.setupListeners(iframe.contentDocument);

            if (progression > 0) {
                const doc = iframe.contentDocument.documentElement;
                iframe.contentWindow.scrollTo(doc.scrollWidth * progression, 0);
            }
        };

        iframe.src = this.currentBaseUrl + targetHref;
    },

    injectAssets: function(doc) {
        if (!doc || !doc.head) return;

        // Inject Readium CSS Before
        this.addStyleLink(doc, '/assets/css/ReadiumCSS-before.css', true);

        // Inject Readium CSS After
        this.addStyleLink(doc, '/assets/css/ReadiumCSS-after.css', false);

        // Inject Default styles to handle pagination
        const style = doc.createElement('style');
        style.textContent = `
            html {
                height: 100vh !important;
                overflow: hidden !important;
            }
            body {
                height: 100vh !important;
            }
        `;
        doc.head.appendChild(style);
    },

    addStyleLink: function(doc, href, prepend) {
        const link = doc.createElement('link');
        link.rel = 'stylesheet';
        link.href = href;
        if (prepend) {
            doc.head.prepend(link);
        } else {
            doc.head.appendChild(link);
        }
    },

    updateSettings: function(settingsJson) {
        const newSettings = JSON.parse(settingsJson);
        this.settings = { ...this.settings, ...newSettings };

        const iframe = document.getElementById('readium-iframe');
        if (iframe && iframe.contentDocument) {
            this.applySettings(iframe.contentDocument);
        }
    },

    applySettings: function(doc) {
        const root = doc.documentElement;
        root.style.setProperty("--USER__appearance", this.settings.appearance);
        root.style.setProperty("--USER__fontSize", this.settings.fontSize);
        root.style.setProperty("--USER__fontFamily", this.settings.fontFamily);
        root.style.setProperty("--USER__colCount", this.settings.columnCount || "1");
        root.style.setProperty("--USER__lineHeight", this.settings.lineHeight || "1.5");
        root.style.setProperty("--USER__pageMargins", this.settings.pageMargins || "1.0");

        // Trigger Readium CSS refresh
        if (this.settings.appearance === 'readium-night-on') {
            doc.body.style.backgroundColor = '#121212';
            doc.body.style.color = '#ffffff';
        } else if (this.settings.appearance === 'readium-sepia-on') {
            doc.body.style.backgroundColor = '#f4ecd8';
            doc.body.style.color = '#5b4636';
        } else {
            doc.body.style.backgroundColor = '#ffffff';
            doc.body.style.color = '#121212';
        }
    },

    setupListeners: function(doc) {
        doc.addEventListener('click', (e) => {
            const width = window.innerWidth;
            const x = e.clientX;

            // Check if clicking links
            let target = e.target;
            while (target && target !== doc.body) {
                if (target.tagName === 'A') {
                    const href = target.getAttribute('href');
                    if (href && !href.startsWith('http')) {
                        e.preventDefault();
                        this.navigateToHref(href);
                        return;
                    }
                }
                target = target.parentElement;
            }

            if (x < width * 0.2) {
                this.prevPage();
            } else if (x > width * 0.8) {
                this.nextPage();
            } else {
                if (window.kmpJsBridge) {
                    window.kmpJsBridge.callNative("onToggleControls", null);
                }
            }
        });

        // Basic Pagination via keyboard
        doc.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowRight' || e.key === ' ') this.nextPage();
            if (e.key === 'ArrowLeft') this.prevPage();
        });
    },

    nextPage: function() {
        const iframe = document.getElementById('readium-iframe');
        if (!iframe) return;

        const doc = iframe.contentDocument.documentElement;
        const currentScroll = doc.scrollLeft;
        const maxScroll = doc.scrollWidth - doc.clientWidth;

        if (currentScroll < maxScroll - 10) {
            iframe.contentWindow.scrollBy({ left: doc.clientWidth, behavior: 'smooth' });
            setTimeout(() => this.reportProgress(), 300);
        } else {
            this.nextResource();
        }
    },

    prevPage: function() {
        const iframe = document.getElementById('readium-iframe');
        if (!iframe) return;

        const doc = iframe.contentDocument.documentElement;
        const currentScroll = doc.scrollLeft;

        if (currentScroll > 10) {
            iframe.contentWindow.scrollBy({ left: -doc.clientWidth, behavior: 'smooth' });
            setTimeout(() => this.reportProgress(), 300);
        } else {
            this.prevResource();
        }
    },

    nextResource: function() {
        if (this.currentIndex < this.currentManifest.readingOrder.length - 1) {
            this.currentIndex++;
            this.loadResource(this.currentIndex, false);
        }
    },

    prevResource: function() {
        if (this.currentIndex > 0) {
            this.currentIndex--;
            this.loadResource(this.currentIndex, true);
        }
    },

    loadResource: function(index, atEnd) {
        this.currentIndex = index;
        const item = this.currentManifest.readingOrder[index];
        const iframe = document.getElementById('readium-iframe');

        const onLoad = () => {
            iframe.removeEventListener('load', onLoad);
            if (atEnd) {
                const doc = iframe.contentDocument.documentElement;
                iframe.contentWindow.scrollTo(doc.scrollWidth, 0);
            }
            this.reportProgress();
        };

        iframe.addEventListener('load', onLoad);
        iframe.src = this.currentBaseUrl + item.href;
    },

    reportProgress: function() {
        const iframe = document.getElementById('readium-iframe');
        if (!iframe) return;

        const doc = iframe.contentDocument.documentElement;
        const scrollLeft = doc.scrollLeft;
        const totalWidth = doc.scrollWidth;
        const viewportWidth = iframe.contentWindow.innerWidth;

        const progression = totalWidth > 0 ? scrollLeft / totalWidth : 0;
        const totalProgress = (this.currentIndex + progression) / this.currentManifest.readingOrder.length;

        const locator = {
            href: this.currentManifest.readingOrder[this.currentIndex].href,
            type: "application/xhtml+xml",
            locations: {
                progression: progression
            }
        };

        if (window.kmpJsBridge) {
            window.kmpJsBridge.callNative("onPositionChanged", JSON.stringify({
                progress: totalProgress,
                locator: JSON.stringify(locator)
            }));
        }
    },

    navigateToHref: function(href) {
        // Handle internal links and CFI/ID anchors
        const parts = href.split('#');
        const resourceHref = parts[0];
        const anchor = parts[1];

        const spine = this.currentManifest.readingOrder;
        const index = spine.findIndex(item => item.href.endsWith(resourceHref) || resourceHref.endsWith(item.href));

        if (index !== -1) {
            this.currentIndex = index;
            const iframe = document.getElementById('readium-iframe');

            const onLoad = () => {
                iframe.removeEventListener('load', onLoad);
                if (anchor) {
                    const element = iframe.contentDocument.getElementById(anchor);
                    if (element) {
                        element.scrollIntoView();
                    }
                }
                this.reportProgress();
            };

            iframe.addEventListener('load', onLoad);
            iframe.src = this.currentBaseUrl + spine[index].href;
        }
    }
};
