package in.wptrafficanalyzer.euecologico2;


public class Pair {

    boolean flag;
    int value;

    public Pair(boolean flag, int value) {
        this.flag = flag;
        this.value = value;
    }

    public boolean getFirst() {
        return flag;
    }

    public int getSecond() {
        return value;
    }
}
