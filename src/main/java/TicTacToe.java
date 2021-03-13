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
public class TicTacToe extends JFrame implements MouseListener{
  private int width, height;
  private int mouseX = 0, mouseY = 0, mapsX = 0, mapsY = 0;
  private int game_width = 600, game_height = 600;
  private BufferedImage bgImage = null;
  private int chessBoardItemWidth = 25;
  private Rectangle chessBoardRect = null;
  private BufferedImage offsetImg = new BufferedImage(game_width, game_height, BufferedImage.TYPE_4BYTE_ABGR);
  private Graphics g = offsetImg.getGraphics();
  private int[][] maps = new int[15][15];
  private boolean isBlack = true;
  private String message = "5 in a Row，horizontally, vertically, or diagonally.";
  private JButton btn_start, btn_exit, btn_settings;
  private JPanel operaterPanel;
  private int gametime = 0;
  private int blackTime = 0, whiteTime = 0;

  public TicTacToe() {
    setTitle("TicTacToe");
    setSize(game_width, game_height);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    width = Toolkit.getDefaultToolkit().getScreenSize().width;
    height = Toolkit.getDefaultToolkit().getScreenSize().height;

    chessBoardRect = new Rectangle(50, 120, 370, 370);

    setLocation((width - game_width) / 2, (height - game_height) / 2);

    addMouseListener(this);

    initOeratePane();

    repaint();

    try {
      bgImage = ImageIO.read(new File("img/backgroung.png"));
      // System.out.println(bgImage);
    } catch (IOException e) {
      e.printStackTrace();
    }
    setVisible(true);
  }

  /**
   * 初始化右边操作界面
   */
  private void initOeratePane() {
    btn_start = new JButton("start game");
    btn_exit = new JButton("exit");
    btn_start.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        int select = JOptionPane.showConfirmDialog(getContentPane(), "Do you want to start over？");
        if (select == 0) {
          reStartGame();
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
    operaterPanel.add(btn_exit);
    getContentPane().add(operaterPanel, BorderLayout.EAST);
  }

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
    g.drawImage(bgImage, 20, 90, this);
    g.setColor(Color.black);
    g.setFont(new Font("Dialog", Font.BOLD, 17));
    g.drawString(message, 10, 75);
    int adjust = 12;
    for (int i = 0; i < 16; i++) {
      g.drawLine(60 - adjust, 130 - adjust + i * chessBoardItemWidth, 410 + adjust, 130 - adjust + i * chessBoardItemWidth);
      g.drawLine(60 + i * chessBoardItemWidth - adjust, 130 - adjust, 60 - adjust + i * chessBoardItemWidth, 480 + adjust);
    }
    for (int j = 0; j < maps.length; j++) {
      for (int i = 0; i < maps[0].length; i++) {
        int x1 = 50 + i * chessBoardItemWidth;
        int y1 = 120 + j * chessBoardItemWidth;
        if (maps[j][i] == 1) {
          g.setColor(Color.blue);
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

    g1.drawImage(offsetImg, 0, 0, this);
  }

  public void mouseClicked(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
    if (chessBoardRect.contains(mouseX, mouseY)) {
      mapsX = (mouseX - 50) / chessBoardItemWidth;
      mapsY = (mouseY - 120) / chessBoardItemWidth;

      if (maps[mapsY][mapsX] == 0) {
        if (isBlack) {
          maps[mapsY][mapsX] = 1;
          isBlack = false;
        } else {
          maps[mapsY][mapsX] = 2;
          isBlack = true;
        }
        repaint();
        checkGame();
      }

    }
  }

  private void checkGame() {
    int color = maps[mapsY][mapsX];
    boolean isWin = false;

    // System.out.println("mapsXY:" + mapsX + "," + mapsY);

    isWin = checkChess(1, 0, color) || checkChess(0, 1, color) || checkChess(1, 1, color)
            || checkChess(1, -1, color);
    if (isWin) {
      if (color == 1)
        JOptionPane.showMessageDialog(this, "X win！");
      else {
        JOptionPane.showMessageDialog(this, "O win！");
      }
      reStartGame();
      // new GameWindow();
    }

  }

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
  }

  public void mouseExited(MouseEvent e) {

  }

  public static void main(String[] args) {
    new TicTacToe();
  }
}