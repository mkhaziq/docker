var path = require("path");

module.exports = {
  entry: "./src/main/js/src/index.js",
  devtool: "inline-source-map",
  mode: "development",
  output: {
    path: __dirname,
    filename: "./src/main/resources/static/built/bundle.js",
  },
  module: {
    rules: [
      {
        test: /\.js?$/,
        exclude: /(node_modules)/,
        use: [
          {
            loader: "babel-loader",
            options: {
              presets: ["@babel/preset-env", "@babel/preset-react"],
            },
          },
		],
	 },{
        test: /\.css$/i,
		use: ["style-loader", "css-loader"],
	},{
       	test: /\.(jpg|png|svg)$/,
        use:[{ 
            	loader: 'url-loader',
                options: {
                	limit: 25000
             	}
          }
		],
      },
    ],
  },
};
