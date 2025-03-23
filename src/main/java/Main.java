

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.Ansi.Erase;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static int w = 10;
    public static int h = 10;
    public static int mineCount = 10;

    public static int x = 0;
    public static int y = 0;

    public static int[][] map;
    public static boolean[][] rev;
    public static boolean[][] mark;

    public static int o = 0;
    public static int m = 0;
    public static long startTime;
    public static long offsetTime = 0;

    public final static Ansi a = Ansi.ansi();
    public final static Scanner s = new Scanner(System.in);
    public final static Random r = new Random();
    public final static int[][] dPos = new int[][] { { 1, 0 }, { -1, 0 },
            { 1, 1 }, { 1, -1 }, { -1, -1 }, { -1, 1 }, { 0, 1 }, { 0, -1 } };
    public static Queue<Pos> q = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        Terminal t = TerminalBuilder.builder()
                .system(true)
                .jna(true)
                .nativeSignals(true)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print("\033[?25h");
        }));

        System.out.println(a.fgBrightBlue().bold() + " == Minesweeper == " + a.reset());
        System.out.println(a.fgBrightCyan() +        "      by Zayrex " + a.reset());

        do {
            System.out.print("Enter Size(Default=10x10):");
            var in = s.nextLine();
            if (in.isEmpty()) {
                break;
            }

            try {
                String[] split = in.split("x");
                if (split.length == 2) {
                    w = Integer.parseInt(split[0]);
                    h = Integer.parseInt(split[1]);

                    if(w >= 10 && h >= 10) break;
                }
            } catch (Exception e) {
                
            }

            System.out.println(a.fgRed() + "Input invaild" + a.reset());
        } while (true);

        do {
            System.out.print("Enter mine count(Default=10):");
            var in = s.nextLine();
            if (in.isEmpty()) {
                break;
            }

            try {
                mineCount = Integer.parseInt(in);
                if(mineCount > 0) break;
            } catch (Exception e) {
                
            }

            System.out.println(a.fgRed() + "Input invaild" + a.reset());
        } while (true);

        map = new int[h][w];
        rev = new boolean[h][w];
        mark = new boolean[h][w];

        // Place mines
        for (int i = 0; i < mineCount; i++) {
            int a, b;
            do {
                a = r.nextInt(w);
                b = r.nextInt(h);
            } while (map[b][a] != 0);

            map[b][a] = -1;
        }

        // Calculate numbers
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (map[j][i] != 0)
                    continue;
                int c = 0;

                for (int k = 0; k < dPos.length; k++) {
                    int di = i + dPos[k][0];
                    int dj = j + dPos[k][1];
                    if (inRange(di, dj)) {
                        if (map[dj][di] == -1)
                            c++;
                    }
                }

                map[j][i] = c;
            }
        }

        t.enterRawMode();

        System.out.print("\033[?25l");

        startTime = System.currentTimeMillis();

        render();

        while (true) {
            var k = t.reader().read();
            if (k == 27) {
                var k1 = t.reader().read();
                if (k1 == 91) {
                    // Direction keys
                    var k2 = t.reader().read();
                    // System.out.println(k2);
                    switch (k2) {
                        // up
                        case 65:
                            if (y - 1 >= 0) {
                                y--;
                                render(x, y + 1);
                                render(x, y);
                            }
                            break;
                        // down
                        case 66:
                            if (y + 1 < h) {
                                y++;
                                render(x, y - 1);
                                render(x, y);
                            }
                            break;
                        // right
                        case 67:
                            if (x + 1 < w) {
                                x++;
                                render(x - 1, y);
                                render(x, y);
                            }
                            break;
                        // left
                        case 68:
                            if (x - 1 >= 0) {
                                x--;
                                render(x + 1, y);
                                render(x, y);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }

            switch (k) {
                // z
                case 122: {
                    if (rev[y][x] == false && mark[y][x] == false) {
                        if (map[y][x] == -1) {
                            // Set off bomb
                            renderLost();
                            System.exit(0);
                        } else if (map[y][x] == 0) {
                            q.add(new Pos(x, y));

                            while (!q.isEmpty()) {
                                Pos cur = q.poll();

                                if (rev[cur.y][cur.x])
                                    continue;

                                rev[cur.y][cur.x] = true;
                                render(cur.x, cur.y);
                                o++;
                                if (map[cur.y][cur.x] != 0)
                                    continue;
                                for (int di = 0; di < dPos.length; di++) {
                                    int xt = cur.x + dPos[di][0];
                                    int yt = cur.y + dPos[di][1];
                                    if (inRange(xt, yt) && map[yt][xt] != -1 && !rev[yt][xt]) {
                                        q.add(new Pos(xt, yt));
                                    }
                                }
                            }
                        } else {
                            rev[y][x] = true;
                            o++;
                            render(x, y);
                        }
                    }
                    break;
                }

                // x
                case 120:
                    if (rev[y][x] == false) {
                        if (mark[y][x])
                            m--;
                        else
                            m++;
                        mark[y][x] = !mark[y][x];
                        render(x, y);
                    }

                    break;
                default:
                    break;
            }

            updateStat();

            if (o == w * h - mineCount) {
                win();
                System.exit(0);
            }
        }
    }

    private static void win() {
        final long e = System.currentTimeMillis();
        final String str = "You won in " + (e - startTime - offsetTime) / 1000 + "s!";
        int m = (h + 3) / 2;
        System.out.print(a.cursor(m - 1, 1));
        System.out.print(a.bgBrightCyan());
        for (int i = 0; i < w + 2; i++) {
            System.out.print(" ");
        }
        System.out.print(a.cursor(m, 1));
        for (int i = 0; i < (w + 2 - str.length()) / 2; i++) {
            System.out.print(" ");
        }
        System.out.print(str);
        for (int i = 0; i < (w + 2) - ((w + 2 - str.length()) / 2 + str.length()); i++) {
            System.out.print(" ");
        }
        System.out.print(a.cursor(m + 1, 1));
        for (int i = 0; i < w + 2; i++) {
            System.out.print(" ");
        }
        System.out.print(a.cursor(h + 4, 1));
    }

    public static void render(int i, int j) {
        final long s = System.currentTimeMillis();
        System.out.print(a.cursor(j + 3, i + 2));
        if (x == i && y == j)
            System.out.print(a.bg(Color.WHITE).fg(Color.BLACK));
        if (rev[j][i]) {
            System.out.print(map[j][i] == 0 ? " " : map[j][i]);
        } else if (mark[j][i]) {
            System.out.print("*");
        } else {
            System.out.print("#");
        }

        System.out.print(a.reset());
        offsetTime += System.currentTimeMillis() - s;
    }

    public static void updateStat() {
        System.out.print(a.cursor(1, 1));
        System.out.print(a.eraseLine(Erase.BACKWARD));
        System.out.print(a.fgBrightBlue() + " Left:" + (mineCount - m) + "/" + mineCount +
                " Opened:" + (w * h - o - mineCount) + "/" + (w * h - mineCount) + "   " + a.reset());
    }

    public static void render() {
        final long s = System.currentTimeMillis();
        updateStat(); 
        
        System.out.print(a.cursor(2, 1));
        System.out.print(a.bold().bgGreen());
        for (int i = 0; i < w + 2; i++) {
            System.out.print("=");
        }
        System.out.print(a.reset() + "\n");
        for (int j = 0; j < h; j++) {
            System.out.print(a.cursor(j + 3, 1));
            System.out.print(a.bold().bgGreen() + "=" + a.reset().cursorRight(1));
            for (int i = 0; i < w; i++) {
                if (rev[j][i]) {
                    System.out.print(map[j][i] == 0 ? " " : map[j][i]);
                } else if (mark[j][i]) {
                    System.out.print("*");
                } else {
                    System.out.print("#");
                }

                System.out.print(a.cursorRight(1).reset());
            }
            System.out.print(a.bold().bgGreen() + "=" + a.reset() + "\n");
        }
        System.out.print(a.cursor(h + 3, 1));
        System.out.print(a.bold().bgGreen());
        for (int i = 0; i < w + 2; i++) {
            System.out.print("=");
        }
        System.out.println(a.reset());
        offsetTime += System.currentTimeMillis() - s;
    }

    public static void renderLost() {
        final long s = System.currentTimeMillis();
        updateStat();
        System.out.print(a.cursor(2, 1));
        System.out.print(a.bold().bgRed());
        for (int i = 0; i < w + 2; i++) {
            System.out.print("=");
        }
        System.out.print(a.reset() + "\n");
        for (int j = 0; j < h; j++) {
            System.out.print(a.cursor(j + 3, 1));
            System.out.print(a.bold().bgRed() + "=" + a.reset().cursorRight(1));
            for (int i = 0; i < w; i++) {
                if (i == x && j == y) {
                    System.out.print(a.bgYellow().fgBlack());
                }
                if (mark[j][i]) {
                    if (map[j][i] == -1) {
                        System.out.print(a.fgBrightGreen() + "*" + a.reset());
                    } else {
                        System.out.print(a.fgBrightBlack() + "%" + a.reset());
                    }
                } else {
                    if (map[j][i] == -1) {
                        System.out.print(a.fgBrightRed() + "!" + a.reset());
                    } else {
                        if (!rev[j][i])
                            System.out.print(a.bg(Color.MAGENTA));
                        System.out.print(map[j][i] == 0 ? " " : map[j][i]);
                    }
                }

                System.out.print(a.cursorRight(1).reset());
            }
            System.out.print(a.bold().bgRed() + "=" + a.reset() + "\n");
        }
        System.out.print(a.cursor(h + 3, 1));
        System.out.print(a.bold().bgRed());
        for (int i = 0; i < w + 2; i++) {
            System.out.print("=");
        }
        System.out.println(a.reset());

        System.out.print(a.cursor(h + 4, 1));
        System.out.print("You lost in " + (s - startTime - offsetTime) / 1000 + "s!");
    }

    static record Pos(int x, int y) {
    }

    public static boolean inRange(int x, int y) {
        return x >= 0 && x < w && y >= 0 && y < h;
    }
}