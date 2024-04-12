module.exports = {
    // Set test environment to "jsdom" for React testing
    testEnvironment: 'jsdom',
    // Tell Jest where to find test files
    testRegex: '.test.js$',
    // Configure `@testing-library/jest-dom` for extended matchers
    setupFilesAfterEnv: ['./jest-setup/setupTests.js'],
    setupTestFrameworkScriptFile: './jest-setup/frameworkSetup.js',
    // Optionally include other configurations like moduleFileExtensions, etc.

    transform: {
        '^.+\\.jsx?$': 'babel-jest', // Transform .js and .jsx files using Babel

    },
    moduleNameMapper: {
        '\\.(jpg|jpeg|png|gif|webp|svg|css)$': 'identity-obj-proxy',
    },
};