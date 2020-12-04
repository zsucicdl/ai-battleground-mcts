package mcts.game;

import java.util.Objects;

public class ValuesXY {
    private int x;
    private int y;

    public ValuesXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuesXY valuesXY = (ValuesXY) o;
        return x == valuesXY.x &&
                y == valuesXY.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
