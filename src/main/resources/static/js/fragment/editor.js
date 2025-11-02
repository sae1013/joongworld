/**
 * This configuration was generated using the CKEditor 5 Builder. You can modify it anytime using this link:
 * https://ckeditor.com/ckeditor-5/builder/#installation/NoNgNARATAdA7DAHBSBGADFVBWVq4AsccqAnCFAMwgEEhyLo1xNQj2VynbbqogoIAawD2KdGGCowE2TLCoAupHSU6iRADMIioA==
 */

const {
    ClassicEditor,
    Autosave,
    Essentials,
    Paragraph,
    MediaEmbed,
    PasteFromOffice,
    ImageUtils,
    ImageEditing,
    Mention,
    Bold,
    Italic,
    Underline,
    Strikethrough,
    Subscript,
    Superscript,
    FontBackgroundColor,
    FontColor,
    FontFamily,
    FontSize,
    Highlight,
    Heading,
    Link,
    AutoLink,
    BlockQuote,
    HorizontalLine,
    Indent,
    IndentBlock,
    Alignment,
    GeneralHtmlSupport,
    HtmlComment,
    List
} = window.CKEDITOR;

const LICENSE_KEY =
    'eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE3NjMxNjQ3OTksImp0aSI6ImU4YWY5NDkxLTAyNmUtNDZiOS04OTJiLWU5MmVjODNjZGExOSIsInVzYWdlRW5kcG9pbnQiOiJodHRwczovL3Byb3h5LWV2ZW50LmNrZWRpdG9yLmNvbSIsImRpc3RyaWJ1dGlvbkNoYW5uZWwiOlsiY2xvdWQiLCJkcnVwYWwiLCJzaCJdLCJ3aGl0ZUxhYmVsIjp0cnVlLCJsaWNlbnNlVHlwZSI6InRyaWFsIiwiZmVhdHVyZXMiOlsiKiJdLCJ2YyI6ImJiYWM2ZTExIn0._mWs3ak1ET-okuaLjd1rOPeG3Eat86PBg7kfUDjRDc6nStZz2uFW7bTJZb-u7xwXDcEhW3OFSlajcKQVBD589A';

const editorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'fontSize',
            'fontFamily',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            'subscript',
            'superscript',
            '|',
            'horizontalLine',
            'link',
            'mediaEmbed',
            'highlight',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'bulletedList',
            'numberedList',
            'outdent',
            'indent'
        ],
        shouldNotGroupWhenFull: true
    },
    plugins: [
        Alignment,
        AutoLink,
        Autosave,
        BlockQuote,
        Bold,
        Essentials,
        FontBackgroundColor,
        FontColor,
        FontFamily,
        FontSize,
        GeneralHtmlSupport,
        Heading,
        Highlight,
        HorizontalLine,
        HtmlComment,
        ImageEditing,
        ImageUtils,
        Indent,
        IndentBlock,
        Italic,
        Link,
        List,
        MediaEmbed,
        Mention,
        Paragraph,
        PasteFromOffice,
        Strikethrough,
        Subscript,
        Superscript,
        Underline
    ],
    fontFamily: {
        supportAllValues: true
    },
    fontSize: {
        options: [10, 12, 14, 'default', 18, 20, 22],
        supportAllValues: true
    },
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    htmlSupport: {
        allow: [
            {
                name: /^.*$/,
                styles: true,
                attributes: true,
                classes: true
            }
        ]
    },
    initialData: '',
    language: 'ko',
    licenseKey: LICENSE_KEY,
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },

    placeholder: '게시글을 작성해주세요.'
};

configUpdateAlert(editorConfig);

const editorTargets = document.querySelectorAll('[data-editor-target]');
if (editorTargets.length) {
    editorTargets.forEach((target) => {
        ClassicEditor.create(target, editorConfig).catch((error) => {
            console.error('[editor] CKEditor 초기화 실패', error);
        });
    });
}

/**
 * This function exists to remind you to update the config needed for premium features.
 * The function can be safely removed. Make sure to also remove call to this function when doing so.
 */
function configUpdateAlert(config) {
    if (configUpdateAlert.configUpdateAlertShown) {
        return;
    }

    const isModifiedByUser = (currentValue, forbiddenValue) => {
        if (currentValue === forbiddenValue) {
            return false;
        }

        if (currentValue === undefined) {
            return false;
        }

        return true;
    };

    const valuesToUpdate = [];

    configUpdateAlert.configUpdateAlertShown = true;

    if (!isModifiedByUser(config.licenseKey, '<YOUR_LICENSE_KEY>')) {
        valuesToUpdate.push('LICENSE_KEY');
    }

    if (valuesToUpdate.length) {
        window.alert(
            [
                'Please update the following values in your editor config',
                'to receive full access to Premium Features:',
                '',
                ...valuesToUpdate.map(value => ` - ${value}`)
            ].join('\n')
        );
    }
}
