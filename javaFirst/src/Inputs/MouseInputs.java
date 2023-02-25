package Inputs;

import java.awt.event.MouseEvent;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MouseInputListener;

import Main.Pannel;
import Main.squrInfo;

public class MouseInputs implements MenuDragMouseListener, MouseInputListener {

    private Pannel pannel;
    private int moveX;
    private int moveY;
    boolean red = false;
    boolean blue = false;
    boolean jTurn = true;

    public MouseInputs(Pannel pannel) {
        this.pannel = pannel;// getting the window on which the cursor is
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /*
         */
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int locationX = pannel.squrinfo[i][j].locationX;
                int locationY = pannel.squrinfo[i][j].locationY;
                int height = pannel.squrinfo[i][j].height;
                int width = pannel.squrinfo[i][j].width;
                if (e.getX() >= locationX && e.getY() >= locationY && e.getX() <= locationX + width
                        && e.getY() <= locationY + height) {// find the square which is clicked
                    disableHighlights();
                    pannel.squrinfo[i][j].highlight = true;
                    if (pannel.squrinfo[i][j].red == true && !jTurn) {// if red
                        disablePaths();
                        if (pannel.squrinfo[i][j].king) {
                            redKingLeftPattern( i,  j);
                            redKingRightPattern( i,  j);
                        }
                        redLeftPattern(i, j);
                        redRightPattern(i, j);
                        moveX = j;
                        moveY = i;
                        red = true;
                        blue = false;
                    } else if (pannel.squrinfo[i][j].blue == true && jTurn) {// if blue
                        disablePaths();
                        if (pannel.squrinfo[i][j].king) {
                            blueKingLeftPattern(i,j);
                            blueKingRightPattern(i,j);
                        }
                        blueLeftPattern(i, j);
                        blueRightPattern(i, j);
                        moveX = j;
                        moveY = i;
                        red = false;
                        blue = true;
                    } else if (pannel.squrinfo[i][j].possibleMove == true) {// if move
                        if (jTurn) {// switch turns
                            jTurn = false;
                        } else {
                            jTurn = true;
                        }
                        disablePaths();
                        if (red) {// if move red
                            pannel.squrinfo[i][j].red = true;
                            DeleteEnemy(i,j);
                        } else if (blue) {// if move blue
                            pannel.squrinfo[i][j].blue = true;
                            DeleteEnemy(i,j);
                        }
                        if (i == 0 || i == 7) {// promote to king if needed
                            pannel.squrinfo[i][j].king = true;
                        }
                        if(pannel.squrinfo[moveY][moveX].king == true){//remain king if already king
                            pannel.squrinfo[i][j].king = true;
                            blue = true;
                            blue = true;
                            pannel.squrinfo[moveY][moveX].king = false;
                        }
                    } else {
                        disablePaths();
                    }
                }
            }
        }
    }

    private void DeleteEnemy(int i,int j) {//the only way to destroy fuckers
        if(i<moveY){
            if (j < moveX) {
                for (int o=moveX,p=moveY; j < o; o--, p--) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
            if (j > moveX) {
                for (int o=moveX,p=moveY; j > o; o++, p--) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
        }
        else{
            if (j < moveX) {
                for (int o=moveX,p=moveY; j < o; o--, p++) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
            if (j > moveX) {
                for (int o=moveX,p=moveY; j > o; o++, p++) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
        }
    }
    private void blueKingLeftPattern(int i, int j) {
        if (j == 0 || i == 7)
            return;
        if (!pannel.squrinfo[i + 1][j - 1].blue && !pannel.squrinfo[i + 1][j - 1].red) {
            pannel.squrinfo[i + 1][j - 1].possibleMove = true;
        }
        if (j - 2 == -1 || i + 2 == 8) {
            return;
        }
        if (pannel.squrinfo[i + 1][j - 1].red && !pannel.squrinfo[i + 2][j - 2].blue
                && !pannel.squrinfo[i + 2][j - 2].red) {
            pannel.squrinfo[i + 2][j - 2].possibleMove = true;
            if (j - 3 == -1 || i + 3 == 8) {
                return;
            }
            if (pannel.squrinfo[i + 3][j - 3].red) {
                blueKingLeftPattern(i + 2, j - 2);
            }
        }
    }

    private void blueKingRightPattern(int i, int j) {
        if (j == 7 || i == 7)
            return;
        if (!pannel.squrinfo[i + 1][j + 1].blue && !pannel.squrinfo[i + 1][j + 1].red) {
            pannel.squrinfo[i + 1][j + 1].possibleMove = true;
        }
        if (j + 2 == 8 || i + 2 == 8) {
            return;
        }
        if (pannel.squrinfo[i + 1][j + 1].red && !pannel.squrinfo[i + 2][j + 2].blue
                && !pannel.squrinfo[i + 2][j + 2].red) {
            pannel.squrinfo[i + 2][j + 2].possibleMove = true;
            if (j + 3 == 8 || i + 3 == 8) {
                return;
            }
            if (pannel.squrinfo[i + 3][j + 3].red) {
                blueKingRightPattern(i + 2, j + 2);
            }
        }
    }
    private void redKingLeftPattern(int i, int j) {
        if (j == 0 || i == 0)
            return;
        if (!pannel.squrinfo[i - 1][j - 1].blue && !pannel.squrinfo[i - 1][j - 1].red) {
            pannel.squrinfo[i - 1][j - 1].possibleMove = true;
        }
        if (j - 2 == -1 || i - 2 == -1) {
            return;
        }
        if (pannel.squrinfo[i - 1][j - 1].blue && !pannel.squrinfo[i - 2][j - 2].blue
                && !pannel.squrinfo[i - 2][j - 2].red) {
            pannel.squrinfo[i - 2][j - 2].possibleMove = true;
            if (j - 3 == -1 || i - 3 == -1) {
                return;
            }
            if (pannel.squrinfo[i - 3][j - 3].blue) {
                redKingLeftPattern(i - 2, j - 2);
            }
        }
    }
    private void redKingRightPattern(int i, int j) {
        if (j == 7 || i == 0)
            return;
        if (!pannel.squrinfo[i - 1][j + 1].blue && !pannel.squrinfo[i - 1][j + 1].red) {
            pannel.squrinfo[i - 1][j + 1].possibleMove = true;
        }
        if (j + 2 == 8 || i - 2 == -1) {
            return;
        }
        if (pannel.squrinfo[i - 1][j + 1].blue && !pannel.squrinfo[i - 2][j + 2].blue
                && !pannel.squrinfo[i - 2][j + 2].red) {
            pannel.squrinfo[i - 2][j + 2].possibleMove = true;
            if (j + 3 == 8 || i - 3 == -1) {
                return;
            }
            if (pannel.squrinfo[i - 3][j + 3].blue) {
                redKingRightPattern(i - 2, j + 2);
            }
        }
    }

    private void redLeftPattern(int i, int j) {
        if (j == 0 || i == 7)
            return;
        if (!pannel.squrinfo[i + 1][j - 1].blue && !pannel.squrinfo[i + 1][j - 1].red) {
            pannel.squrinfo[i + 1][j - 1].possibleMove = true;
        }
        if (j - 2 == -1 || i + 2 == 8) {
            return;
        }
        if (pannel.squrinfo[i + 1][j - 1].blue && !pannel.squrinfo[i + 2][j - 2].blue
                && !pannel.squrinfo[i + 2][j - 2].red) {
            pannel.squrinfo[i + 2][j - 2].possibleMove = true;
            if (j - 3 == -1 || i + 3 == 8) {
                return;
            }
            if (pannel.squrinfo[i + 3][j - 3].blue) {
                redLeftPattern(i + 2, j - 2);
            }
        }
    }

    private void redRightPattern(int i, int j) {
        if (j == 7 || i == 7)
            return;
        if (!pannel.squrinfo[i + 1][j + 1].blue && !pannel.squrinfo[i + 1][j + 1].red) {
            pannel.squrinfo[i + 1][j + 1].possibleMove = true;
        }
        if (j + 2 == 8 || i + 2 == 8) {
            return;
        }
        if (pannel.squrinfo[i + 1][j + 1].blue && !pannel.squrinfo[i + 2][j + 2].blue
                && !pannel.squrinfo[i + 2][j + 2].red) {
            pannel.squrinfo[i + 2][j + 2].possibleMove = true;
            if (j + 3 == 8 || i + 3 == 8) {
                return;
            }
            if (pannel.squrinfo[i + 3][j + 3].blue) {
                redRightPattern(i + 2, j + 2);
            }

        }
    }

    private void blueLeftPattern(int i, int j) {
        if (j == 0 || i == 0)
            return;
        if (!pannel.squrinfo[i - 1][j - 1].blue && !pannel.squrinfo[i - 1][j - 1].red) {
            pannel.squrinfo[i - 1][j - 1].possibleMove = true;
        }
        if (j - 2 == -1 || i - 2 == -1) {
            return;
        }
        if (pannel.squrinfo[i - 1][j - 1].red && !pannel.squrinfo[i - 2][j - 2].blue
                && !pannel.squrinfo[i - 2][j - 2].red) {
            pannel.squrinfo[i - 2][j - 2].possibleMove = true;
            if (j - 3 == -1 || i - 3 == -1) {
                return;
            }
            if (pannel.squrinfo[i - 3][j - 3].red) {
                blueLeftPattern(i - 2, j - 2);
            }
        }
    }

    private void blueRightPattern(int i, int j) {
        if (j == 7 || i == 0)
            return;
        if (!pannel.squrinfo[i - 1][j + 1].blue && !pannel.squrinfo[i - 1][j + 1].red) {
            pannel.squrinfo[i - 1][j + 1].possibleMove = true;
        }
        if (j + 2 == 8 || i - 2 == -1) {
            return;
        }
        if (pannel.squrinfo[i - 1][j + 1].red && !pannel.squrinfo[i - 2][j + 2].blue
                && !pannel.squrinfo[i - 2][j + 2].red) {
            pannel.squrinfo[i - 2][j + 2].possibleMove = true;
            if (j + 3 == 8 || i - 3 == -1) {
                return;
            }
            if (pannel.squrinfo[i - 3][j + 3].red) {
                blueRightPattern(i - 2, j + 2);
            }

        }
    }

    private void disablePaths() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pannel.squrinfo[i][j].possibleMove = false;
            }
        }
    }

    private void disableHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pannel.squrinfo[i][j].highlight = false;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void menuDragMouseDragged(MenuDragMouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuDragMouseEntered(MenuDragMouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuDragMouseExited(MenuDragMouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuDragMouseReleased(MenuDragMouseEvent e) {
        // TODO Auto-generated method stub

    }

}
