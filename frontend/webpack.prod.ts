import dotenv from "dotenv";
import merge from "webpack-merge";

import path from "path";

import common from "./webpack.common";

dotenv.config({ path: ".env.production" });

const config = merge(common, {
  entry: "./src",
  output: {
    path: path.resolve(__dirname, "dist/client"),
    filename: "[name].js",
    publicPath: "/",
    clean: true,
  },
  devtool: false,
});

export default config;
