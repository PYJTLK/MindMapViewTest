package com.pyjtlk.widget;

public class Direction{
    /**
     * 方向：从左到右
     */
    public static final int DIRECTION_LEFT_TO_RIGHT = 0;

    /**
     * 方向：从右到左
     */
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;

    /**
     * 方向：从上到下
     */
    public static final int DIRECTION_UP_TO_DOWN = 2;

    /**
     * 方向：从下到上
     */
    public static final int DIRECTION_DOWN_TO_UP = 3;

    public static boolean isHorizontal(Direction direction){
        return direction.direction == DIRECTION_LEFT_TO_RIGHT || direction.direction == DIRECTION_RIGHT_TO_LEFT;
    }

    public static boolean isVertical(Direction direction){
        return direction.direction == DIRECTION_LEFT_TO_RIGHT || direction.direction == DIRECTION_RIGHT_TO_LEFT;
    }

    public int direction;
}
