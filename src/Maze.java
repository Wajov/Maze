import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Maze extends JFrame {
    private class Score implements Comparable<Score> {
        private String name;
        private int time, step;
        private Score(String name, int time, int step) {
            this.name = name;
            this.time = time;
            this.step = step;
        }

        @Override
        public int compareTo(Score o) {
            if (time != o.time)
                return time - o.time;
            else if (step != o.step)
                return step - o.step;
            else
                return name.compareTo(o.name);
        }
    }
    private static Random Rand = new Random();
    private final static int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
    private int Step, Time, x, y, startX, startY, finishX, finishY;
    private int[] a;
    private boolean[][][] flag;
    private ArrayList<Score> board;
    private JPanel contentPane, panel1, panel2;
    private JDialog dialog1, dialog2;
    private JButton button, button1, button2;
    private JLabel label;
    private JTextField textField;
    private JScrollPane scrollPane;
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Maze frame = new Maze();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Maze() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Maze");
        setBounds(100, 100, 630, 460);

        contentPane = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, 630, 460);

                g.setColor(Color.WHITE);
                g.setFont(new Font("微软雅黑", Font.PLAIN, 20));
                g.drawString("时间", 30, 270);
                g.drawString("步数", 30, 350);
                g.setColor(Color.RED);
                g.drawString(Integer.toString(Time), 30, 300);
                g.drawString(Integer.toString(Step), 30, 380);

                g.setColor(Color.BLUE);
                for (int i = 1; i < 21; i++)
                    for (int j = 1; j < 26; j++) {
                        if (!flag[i][j][0])
                            g.drawLine(j * 20 + 80, i * 20 - 10, j * 20 + 100, i * 20 - 10);
                        if (!flag[i][j][1])
                            g.drawLine(j * 20 + 80, i * 20 + 10, j * 20 + 100, i * 20 + 10);
                        if (!flag[i][j][2])
                            g.drawLine(j * 20 + 80, i * 20 - 10, j * 20 + 80, i * 20 + 10);
                        if (!flag[i][j][3])
                            g.drawLine(j * 20 + 100, i * 20 - 10, j * 20 + 100, i * 20 + 10);
                    }
                g.setColor(Color.RED);
                g.fillOval(y * 20 + 84, x * 20 - 6, 12, 12);
                g.setColor(Color.YELLOW);
                g.fillOval(finishY * 20 + 84, finishX * 20 - 6, 12, 12);
            }
        };
        setContentPane(contentPane);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        contentPane.setFocusable(true);
        contentPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int d = -1;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        d = 0;
                        break;
                    case KeyEvent.VK_DOWN:
                        d = 1;
                        break;
                    case KeyEvent.VK_LEFT:
                        d = 2;
                        break;
                    case KeyEvent.VK_RIGHT:
                        d = 3;
                        break;
                }
                if (d > -1 && flag[x][y][d]) {
                    x += dx[d];
                    y += dy[d];
                    contentPane.repaint();
                    if (x == finishX && y == finishY) {
                        //dispose();
                        dialog1.setVisible(true);
                    }
                    Step++;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        button = new JButton("排行榜");
        button.setBounds(10, 200, 80, 21);
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog2.setVisible(true);
            }
        });
        contentPane.add(button);

        int sum = 500, xt, yt, dir, t1, t2;
        a = new int[501];
        for (int i = 1; i < 501; i++)
            a[i] = i;
        flag = new boolean[21][26][4];
        while (sum > 1) {
            xt = Rand.nextInt(20) + 1;
            yt = Rand.nextInt(25) + 1;
            dir = Rand.nextInt(4);
            while (!judge(xt + dx[dir], yt + dy[dir]))
                dir = Rand.nextInt(4);
            t1 = getRoot(hash(xt, yt));
            t2 = getRoot(hash(xt + dx[dir], yt + dy[dir]));
            if (t1 != t2) {
                a[t1] = t2;
                flag[xt][yt][dir] = flag[xt + dx[dir]][yt + dy[dir]][dir ^ 1] = true;
                sum--;
            }
        }

        Step = Time = 0;
        do {
            startX = Rand.nextInt(20) + 1;
            startY = Rand.nextInt(25) + 1;
            finishX = Rand.nextInt(20) + 1;
            finishY = Rand.nextInt(25) + 1;
        } while (getDistance(startX, startY, finishX, finishY) < 40);
        x = startX;
        y = startY;

        dialog1 = new JDialog();
        dialog1.setTitle("恭喜您到达终点！");
        dialog1.setBounds(100, 100, 250, 125);
        panel1 = new JPanel();
        dialog1.setContentPane(panel1);
        panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel1.setLayout(null);
        label = new JLabel("请输入您想在排行榜中显示的名字：");
        label.setBounds(10, 5, 250, 21);
        textField = new JTextField();
        textField.setBounds(10, 31, 210, 21);
        button1 = new JButton("确认");
        button1.setBounds(75, 60, 80, 21);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BufferedWriter f = new BufferedWriter(new FileWriter("High Score.txt", true));
                    f.append(textField.getText()).append(" ").append(String.valueOf(Time)).append(" ").append(String.valueOf(Step)).append("\n");
                    f.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                dialog1.setVisible(false);
                dialog2.setVisible(true);
            }
        });
        panel1.add(label);
        panel1.add(textField);
        panel1.add(button1);

        dialog2 = new JDialog();
        dialog2.setTitle("排行榜");
        dialog2.setBounds(100, 100, 400, 400);
        panel2 = new JPanel();
        dialog2.setContentPane(panel2);
        dialog2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                try {
                    BufferedReader f = new BufferedReader(new FileReader("High Score.txt"));
                    String line;
                    board = new ArrayList<Score>();
                    while ((line = f.readLine()) != null) {
                        String[] t = line.split(" ");
                        board.add(new Score(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2])));
                    }
                    Collections.sort(board);
                    table.setModel(new DefaultTableModel(
                            new Object[][] {
                            },
                            new String[] {
                                    "玩家", "时间", "步数"
                            }
                    ));
                    for (Score tmp : board)
                        ((DefaultTableModel)table.getModel()).addRow(new Object[] {
                                tmp.name, tmp.time, tmp.step
                        });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel2.setLayout(null);
        scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 360, 300);
        table = new JTable();
        table.setEnabled(false);
        table.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "玩家", "时间", "步数"
                }
        ));
        scrollPane.setViewportView(table);
        button2 = new JButton("确认");
        button2.setBounds(150, 330, 80, 21);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog2.setVisible(false);
            }
        });
        panel2.add(scrollPane);
        panel2.add(button2);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Time++;
                contentPane.repaint();
            }
        }).start();
    }

    public int hash(int x, int y) {
        return (x - 1) * 25 + y;
    }

    public boolean judge(int x, int y) {
        return x > 0 && x < 21 && y > 0 && y < 26;
    }

    public int getRoot(int x) {
        if (a[x] != x)
            a[x] = getRoot(a[x]);
        return a[x];
    }

    public int getDistance(int x1, int y1, int x2, int y2) {
        ArrayList<Integer> qx = new ArrayList<Integer>(), qy = new ArrayList<Integer>();
        int[][] d = new int[21][26];
        for (int i = 1; i < 21; i++)
            for (int j = 1; j < 26; j++)
                d[i][j] = Integer.MAX_VALUE;
        d[x1][y1] = 0;
        qx.add(x1);
        qy.add(y1);
        while (!qx.isEmpty() || !qy.isEmpty()) {
            int nowx = qx.get(0), nowy = qy.get(0);
            qx.remove(0);
            qy.remove(0);
            for (int i = 0; i < 4; i++)
                if (judge(nowx + dx[i], nowy + dy[i]) && d[nowx][nowy] + 1 < d[nowx + dx[i]][nowy + dy[i]]) {
                    d[nowx + dx[i]][nowy + dy[i]] = d[nowx][nowy] + 1;
                    qx.add(nowx + dx[i]);
                    qy.add(nowy + dy[i]);
                }
        }
        return d[x2][y2];
    }
}
