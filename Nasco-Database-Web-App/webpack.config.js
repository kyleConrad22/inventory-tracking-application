const path = require('path')

module.exports = {
    devtool: 'inline-source-map',
    entry: 'app.jsx',
    output: {
        path: path.resolve(__dirname, './src/main/resources/static/dist'),
        filename: 'react-app.js',
        publicPath: '/',
        sourceMapFilename: 'react-app.js.map'
    },
    mode: 'development',
    module: {
        rules: [{
            test: /\.(js|jsx|tsx)$/,
            exclude: /node_modules/,
            loader: "babel-loader",
            options: {
                presets: ['@babel/preset-env', '@babel/preset-react', '@babel/preset-typescript']
            }
        }, {
            test: /\.css$/,
            exclude: /node_modules/,
            use: ['style-loader', 'css-loader']
        }]
    },
    resolve: {
        extensions: ['.js', '.jsx', '.css', '.tsx']
    }, 
    devServer: {
        historyApiFallback: true
    }
}