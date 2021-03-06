import { DefaultTheme } from "styled-components";
import { Common } from "types/styled/common";
import { Components } from "types/styled/components";

import { COLOR } from "utils/constants/color";

const components: Components = {
  button: {
    primary: {
      bg: COLOR.INDIGO_500,
      color: COLOR.WHITE,
      activeBg: COLOR.INDIGO_900,
    },
    secondary: {
      bg: "inherit",
      color: COLOR.GRAY_700,
      activeBg: COLOR.GRAY_200,
    },
  },
  navigation: {
    bg: COLOR.WHITE,
    color: COLOR.GRAY_700,
  },
  chip: {
    primary: {
      bg: COLOR.INDIGO_500,
      color: COLOR.BLACK,
    },
    career: { bg: "rgb(197, 202, 233)", color: COLOR.BLACK },
    count: {
      bg: "rgb(225, 190, 231)",
      color: COLOR.BLACK,
    },
    averageReview: {
      bg: "rgb(248, 187, 208)",
      color: COLOR.BLACK,
    },
  },
};

const common: Common = {
  color: {
    primary: COLOR.INDIGO_500,
    secondary: "inherit",
    dark: COLOR.BLACK,
    light: COLOR.WHITE,
    success: COLOR.GREEN_500,
    error: COLOR.RED_500,
  },
  shape: {
    rounded: "0.25rem",
    pill: "6.25rem",
    circle: "100%",
  },
  boxShadow: {
    primary: "0 2px 5px -1px rgba(0, 0, 0, .25), rgba(0, 0, 0, .3) 0 1px 3px -1px",
    bumped: "7px 7px 15px rgba(0, 0, 0, .15), 0 0 3px 0 rgba(0, 0, 0, .3)",
    pressed: "0 1px 3px rgba(0, 0, 0, .12), 0 1px 2px rgba(0, 0, 0, .25)",
  },
  zIndex: {
    toast: 111,
    modal: 99,
    header: 88,
    menuItem: 77,
  },
  layout: {
    sm: "480px",
    md: "768px",
    lg: "1024px",
  },
};

export const THEME: DefaultTheme = {
  components,
  common,
};
