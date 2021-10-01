module.exports = {
    entry: './src/main/js/app.js',
    devtool: 'source-map',
    cache: true,
    mode: 'development',
    output: {
        filename: 'react-app.js'
    },
    module: {
        rules: [{
            test: /\.(js|jsx)$/,
            exclude: /node_modules/,
            loader: "babel-loader",
            options: {
                presets: ['@babel/preset-env', '@babel/preset-react']
            }
        }]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    }
}