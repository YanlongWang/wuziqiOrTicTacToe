import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Yanlong Wang (wangyanlong0107@gmail.com)
 * @date 03/13/2020
 * @version V1.0
 */
public class TicTacToe extends JFrame implements MouseListener, Runnable {
  private int width, height;// 屏幕宽高
  private int mouseX = 0, mouseY = 0, mapsX = 0, mapsY = 0;// 鼠标坐标，鼠标在地图中的位置
  private int game_width = 600, game_height = 600;// 游戏窗口大小
  private BufferedImage bgImage = null;// 背景图片
  private int chessBoardItemWidth = 25;// 棋盘每一小格的大小
  private Rectangle chessBoardRect = null;// 棋盘所在矩形
  private BufferedImage offsetImg = new BufferedImage(game_width, game_height, BufferedImage.TYPE_4BYTE_ABGR);
  private Graphics g = offsetImg.getGraphics();// 双缓冲解决闪烁问题
  private int[][] maps = new int[15][15];// 0无棋子，1黑子，2白子
  private boolean isBlack = true;// 是否是黑方的回合
  private String message = "黑方先行", whitemessage = "无限制", blackmessage = "无限制";// 界面上方信息，下方时间信息
  // 右边操作界面
  private JButton btn_start, btn_exit, btn_settings;
  private JPanel operaterPanel;// 操作面板

  private int gametime = 0;// 游戏时间限制（秒）
  private int blackTime = 0, whiteTime = 0;// 黑白方剩余时间

  private Thread timeThread = new Thread(this);// 黑白双方倒计时线程
  // private boolean isLimitTime = false;

  public TicTacToe() {
    setTitle("TicTacToe");
    setSize(game_width, game_height);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // 获取屏幕宽高
    width = Toolkit.getDefaultToolkit().getScreenSize().width;
    height = Toolkit.getDefaultToolkit().getScreenSize().height;

    // 棋盘位置矩形
    chessBoardRect = new Rectangle(50, 120, 370, 370);

    setLocation((width - game_width) / 2, (height - game_height) / 2);

    addMouseListener(this);

    // 初始化右边的面板
    initOeratePane();

    repaint();

    // 设置背景
    try {
      bgImage = ImageIO.read(new File("img/backgroung.png"));
      // System.out.println(bgImage);
    } catch (IOException e) {
      e.printStackTrace();
    }
    setVisible(true);
  }

  /**
   * 初始化黑白双方时间
   */
  private void initTime() {
    // System.out.println("isLimitTime:" + isLimitTime);
    if (gametime > 0) {

      timeThread.start();

      if (blackTime < 0) {
        JOptionPane.showMessageDialog(this, "黑方时间已到，白方获胜!");
        timeThread.interrupt();
      } else if (whiteTime < 0) {
        JOptionPane.showMessageDialog(this, "白方时间已到，黑方获胜!");
        timeThread.interrupt();
      }
    }
  }

  /**
   * 初始化右边操作界面
   */
  private void initOeratePane() {
    btn_start = new JButton("开始游戏");
    btn_settings = new JButton("游戏设置");
    btn_exit = new JButton("退出游戏");
    btn_start.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        int select = JOptionPane.showConfirmDialog(getContentPane(), "确定要重新开始吗？");
        if (select == 0) {
          reStartGame();
        }

      }
    });
    btn_settings.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        String select = "";
        select = JOptionPane.showInputDialog("请输入游戏时间（分钟），输入0不限时间：");
        if (select != null && !select.equals("")) {
          try {
            gametime = Integer.parseInt(select) * 60;
            //  System.out.println("gametime:" + gametime);
            //  isLimitTime = true;
            //  System.out.println("设置isLimitTime--" + isLimitTime);
            blackTime = gametime;
            whiteTime = gametime;
            if (gametime > 0) {
              blackmessage = blackTime / 3600 + ":" + blackTime / 60 % 60 + ":" + blackTime % 60;
              whitemessage = whiteTime / 3600 + ":" + whiteTime / 60 % 60 + ":" + whiteTime % 60;
              //  timeThread.resume();
            } else {
              whitemessage = "无限制";
              blackmessage = "无限制";
            }
            initTime();
            repaint();
          } catch (Exception e2) {
            e2.printStackTrace();
            JOptionPane.showMessageDialog(getContentPane(), "请输入正确信息！");
          }

          //
        }
      }
    });
    btn_exit.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    operaterPanel = new JPanel();
    GridLayout layout = new GridLayout(0, 1, 100, 100);
    operaterPanel.setLayout(layout);
    operaterPanel.add(btn_start);
    operaterPanel.add(btn_settings);
    operaterPanel.add(btn_exit);
    getContentPane().add(operaterPanel, BorderLayout.EAST);
  }

  /**
   * 重新开始游戏
   */
  protected void reStartGame() {
    isBlack = true;
    blackTime = gametime;
    whiteTime = gametime;
    // for (int i = 0; i < maps[0].length; i++) {
    // for (int j = 0; j < maps.length; j++) {
    // maps[i][j] = 0;
    // }
    // }
    maps = new int[15][15];
    repaint();
  }

  @Override
  public void paint(Graphics g1) {
    super.paint(g);
    // 绘制背景
    g.drawImage(bgImage, 20, 90, this);
    // 绘制上方标题
    g.setColor(Color.black);
    g.setFont(new Font("楷体", Font.BOLD, 30));
    g.drawString("游戏信息：" + message, 100, 75);
    // 绘制下方
    g.setColor(Color.gray);
    g.fillRect(50, 530, 200, 50);
    g.fillRect(300, 530, 200, 50);
    g.setColor(Color.black);
    g.setFont(new Font("宋体", Font.BOLD, 20));
    g.drawString("黑方时间：" + blackmessage, 60, 560);
    g.drawString("白方时间：" + whitemessage, 310, 560);
    // g.setColor(Color.blue);
    // 绘制棋盘线条
    int adjust = 12;
    for (int i = 0; i < 16; i++) {
      g.drawLine(60 - adjust, 130 - adjust + i * chessBoardItemWidth, 410 + adjust, 130 - adjust + i * chessBoardItemWidth);
      g.drawLine(60 + i * chessBoardItemWidth - adjust, 130 - adjust, 60 - adjust + i * chessBoardItemWidth, 480 + adjust);
    }
    // 标注点位
//    g.fillOval(131, 200, 8, 8);
//    g.fillOval(331, 200, 8, 8);
//    g.fillOval(131, 400, 8, 8);
//    g.fillOval(331, 400, 8, 8);
//    g.fillOval(230, 299, 10, 10);

    // 绘制棋子
    for (int j = 0; j < maps.length; j++) {
      for (int i = 0; i < maps[0].length; i++) {
        int x1 = 50 + i * chessBoardItemWidth;
        int y1 = 120 + j * chessBoardItemWidth;
        if (maps[j][i] == 1) {
          g.setColor(Color.blue);
//          g.fillOval(50 + i * chessBoardItemWidth, 120 + j * chessBoardItemWidth, 20, 20);
          int x2 = 50 + i * chessBoardItemWidth + chessBoardItemWidth - 1;
          int y2 = 120 + j * chessBoardItemWidth + chessBoardItemWidth - 1;
          g.drawLine(x1, y1,x2, y2);
          g.drawLine(x1, y2,x2, y1);
        }
        if (maps[j][i] == 2) {
          g.setColor(Color.red);
          g.drawOval(x1, y1, 20, 20);
        }
      }
    }

    // 双缓冲解决屏幕闪烁
    g1.drawImage(offsetImg, 0, 0, this);
  }

  public void mouseClicked(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
    // 鼠标落子
    if (chessBoardRect.contains(mouseX, mouseY)) {
      mapsX = (mouseX - 50) / chessBoardItemWidth;
      mapsY = (mouseY - 120) / chessBoardItemWidth;
      // System.out.println("mapsXY:" + mapsX + "," + mapsY);
      // maps[mapsY][mapsX] = (isBlack == true ? 1 : 2);

      if (maps[mapsY][mapsX] == 0) {
        if (isBlack) {
          maps[mapsY][mapsX] = 1;
          isBlack = false;
          message = "白色落子";
        } else {
          maps[mapsY][mapsX] = 2;
          isBlack = true;
          message = "黑色落子";
        }
        checkGame();
      }

    }
    repaint();
  }

  /**
   * 判断游戏是否结束
   */
  private void checkGame() {
    int color = maps[mapsY][mapsX];
    boolean isWin = false;

    // System.out.println("mapsXY:" + mapsX + "," + mapsY);

    isWin = checkChess(1, 0, color) || checkChess(0, 1, color) || checkChess(1, 1, color)
            || checkChess(1, -1, color);
    if (isWin) {
      if (color == 1)
        JOptionPane.showMessageDialog(this, "黑方胜利！");
      else {
        JOptionPane.showMessageDialog(this, "白方胜利！");
      }
      reStartGame();
      // new GameWindow();
    }

  }

  /**
   * @param xChange 只能是1,0,-1
   * @param yChange 只能是1,0,-1
   * @param color
   */
  private boolean checkChess(int xChange, int yChange, int color) {
    boolean isWin = false;

    int count = 1, tempX = xChange, tempY = yChange;
    while ((mapsX + tempX) >= 0 && (mapsX + tempX) < 15 && (mapsY + tempY) >= 0 && (mapsY + tempY) < 15
           && maps[mapsY + tempY][mapsX + tempX] == color) {
      count++;
      if (tempX == 0 && tempY == 0)
        break;
      if (tempX > 0)
        tempX++;
      if (tempX < 0)
        tempX--;
      if (tempY > 0)
        tempY++;
      if (tempY < 0)
        tempY--;
    }
    tempX = xChange;
    tempY = yChange;
    while ((mapsX - tempX) >= 0 && (mapsX - tempX) < 15 && (mapsY - tempY) >= 0 && (mapsY - tempY) < 15
           && maps[mapsY - tempY][mapsX - tempX] == color) {
      count++;
      if (tempX == 0 && tempY == 0)
        break;
      if (tempX > 0)
        tempX++;
      if (tempX < 0)
        tempX--;
      if (tempY > 0)
        tempY++;
      if (tempY < 0)
        tempY--;
    }
    // System.out.println("count:" + count);
    if (count >= 5) {
      return true;
    }
    return isWin;
  }

  public void mousePressed(MouseEvent e) {

  }

  public void mouseReleased(MouseEvent e) {

  }

  public void mouseEntered(MouseEvent e) {
    // mouseX = e.getX();
    // mouseY = e.getY();
    // System.out.println("鼠标进入游戏窗口");
    // System.out.println("鼠标坐标：" + mouseX + "," + mouseY);
    // if (chessBoardRect.contains(mouseX, mouseY)) {
    // System.out.println("进入棋盘");
    // if (isBlack) {
    // g.setColor(Color.black);
    // } else {
    // g.setColor(Color.white);
    // }
    // g.fillOval(mouseX, mouseY, 20, 20);
    // repaint();
    // }
  }

  public void mouseExited(MouseEvent e) {

  }

  public void run() {
    while (true) {
      // System.out.println("isblack:" + isBlack);
      if (isBlack) {
        blackTime--;
      } else {
        whiteTime--;
      }
      blackmessage = blackTime / 3600 + ":" + blackTime / 60 % 60 + ":" + blackTime % 60;
      whitemessage = whiteTime / 3600 + ":" + whiteTime / 60 % 60 + ":" + whiteTime % 60;
      // System.out.println("blackTime:" + blackTime);
      // System.out.println("whiteTime:" + whiteTime);
      repaint();
      if (blackTime < 0) {
        JOptionPane.showMessageDialog(getContentPane(), "黑方时间已到，白方获胜!");
        timeThread.interrupt();
        new TicTacToe();
        break;
      } else if (whiteTime < 0) {
        JOptionPane.showMessageDialog(getContentPane(), "白方时间已到，黑方获胜!");
        timeThread.interrupt();
        new TicTacToe();
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  public static void main(String[] args) {
    TicTacToe ticTacToe = new TicTacToe();
  }
}