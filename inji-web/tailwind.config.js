/** @type {import('tailwindcss').Config} */
module.exports = {
    darkMode: 'selector',
    content: [
        "./src/**/**/*.{js,jsx,ts,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                light: {
                    background: '#FFFFFF',
                    header: '#000000',
                    title: '#000000',
                    subTitle: '#717171',
                    searchTitle: '#3E3E3E',
                    primary: '#EB6F2D',
                    helpAccordionHover: '#e0e0e0',
                    shadow: '#18479329',
                    navigationBar: '#F2FBFF',
                    languageIcon: '#EB6F2D',
                    closeIcon: '#8E8E8E',
                    searchIcon: '#8E8E8E',
                    tileBackground: '#FFFFFF',
                    shieldSuccessIcon: '#4b9d1f',
                    shieldErrorIcon: '#EF4444',
                    shieldLoadingIcon: '#ff914b',
                    shieldSuccessShadow: '#f1f7ee',
                    shieldErrorShadow: '#FEF2F2',
                    shieldLoadingShadow: '#f6dfbe',
                },
                dark: {
                    background: '#9DB2BF',
                    header: '#000000',
                    title: '#000000',
                    subTitle: '#717171',
                    searchTitle: '#3E3E3E',
                    primary: '#EB6F2D',
                    helpAccordionHover: '#e0e0e0',
                    shadow: '#526D82',
                    navigationBar: '#DDE6ED',
                    languageIcon: '#EB6F2D',
                    closeIcon: '#8E8E8E',
                    searchIcon: '#8E8E8E',
                    tileBackground: '#DDE6ED',
                    shieldSuccessIcon: '#4b9d1f',
                    shieldErrorIcon: '#EF4444',
                    shieldLoadingIcon: '#EF4444',
                    shieldSuccessShadow: '#f1f7ee',
                    shieldErrorShadow: '#FEF2F2',
                    shieldLoadingShadow: '#FEF2F2',
                }
            }
        },
    },
    plugins: [],
}
