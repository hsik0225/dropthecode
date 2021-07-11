import HtmlWebpackPlugin from "html-webpack-plugin";
import webpack from "webpack";

import path from "path";

const config: webpack.Configuration = {
  entry: "./src/index.tsx",
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "[name].[chunkhash].js",
    clean: true,
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
        },
      },
      {
        test: /\.png/,
        type: "asset/resource",
      },
    ],
  },
  resolve: { extensions: [".js", ".ts", ".tsx"] },
  plugins: [new HtmlWebpackPlugin({ template: "public/index.html", favicon: "public/favicon.ico" })],
};

export default config;
