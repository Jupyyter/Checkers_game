package Inputs;

import java.awt.event.MouseEvent;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MouseInputListener;
import javax.swing.SwingUtilities;

import Main.Pannel;
import Main.squrInfo;

public class MouseInputs implements MenuDragMouseListener, MouseInputListener {

    private Pannel pannel;
    private int moveX, moveY;// last coordinates used
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
        if (SwingUtilities.isLeftMouseButton(e)) {
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
                        // https://cdn.discordapp.com/attachments/969231182304784446/1105212842648547478/image.png
                        if (pannel.squrinfo[i][j].red == true && !jTurn) {// if red
                            //can't move red if !red turn
                            // https://cdn.discordapp.com/attachments/969231182304784446/1105214773500592278/image.png
                            disablePaths();
                            if (pannel.squrinfo[i][j].king) {
                                kingPattern(i, j, "left", "red");
                                kingPattern(i, j, "right", "red");
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105214387494592573/image.png
                            }
                            normalPattern(i, j, "left", "red");
                            normalPattern(i, j, "right", "red");
                            // https://cdn.discordapp.com/attachments/969231182304784446/1105214179654254672/image.png
                            moveX = j;
                            moveY = i;
                            red = true;
                            blue = false;
                        } else if (pannel.squrinfo[i][j].blue == true && jTurn) {// if blue
                            //can't move blue if !blue turn
                            // https://cdn.discordapp.com/attachments/969231182304784446/1105215063234728007/image.png
                            disablePaths();
                            if (pannel.squrinfo[i][j].king) {
                                kingPattern(i, j, "left", "blue");
                                kingPattern(i, j, "right", "blue");
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105213959868514425/image.png
                            }
                            normalPattern(i, j, "left", "blue");
                            normalPattern(i, j, "right", "blue");
                            // https://cdn.discordapp.com/attachments/969231182304784446/1105213590459400293/image.png
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
                                DeleteEnemy(i, j);
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105215910060511343/image.png
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105216187408863352/image.png
                            } else if (blue) {// if move blue
                                pannel.squrinfo[i][j].blue = true;
                                DeleteEnemy(i, j);
                            }
                            if (i == 0 || i == 7) {// promote to king if needed
                                pannel.squrinfo[i][j].king = true;
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105216337342636232/image.png
                                // https://cdn.discordapp.com/attachments/969231182304784446/1105216442745487441/image.png
                            }
                            if (pannel.squrinfo[moveY][moveX].king == true) {// remain king if already king
                                pannel.squrinfo[i][j].king = true;
                                blue = true;
                                pannel.squrinfo[moveY][moveX].king = false;
                            }
                        } else {
                            disablePaths();
                        }
                    }
                }
            }
        } else {
            disableHighlights();
            disablePaths();
        }
    }

    private void DeleteEnemy(int i, int j) {// the only way to destroy fuckers
        if (i < moveY) {
            if (j < moveX) {
                for (int o = moveX, p = moveY; j < o; o--, p--) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
            if (j > moveX) {
                for (int o = moveX, p = moveY; j > o; o++, p--) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
        } else {
            if (j < moveX) {
                for (int o = moveX, p = moveY; j < o; o--, p++) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
            if (j > moveX) {
                for (int o = moveX, p = moveY; j > o; o++, p++) {// destroy enemy
                    pannel.squrinfo[p][o].red = false;
                    pannel.squrinfo[p][o].blue = false;
                }
            }
        }
    }

    private void kingPattern(int i, int j, String direction, String color) {
        int rowModifier = color.equals("blue") ? 1 : -1;
        int colModifier = direction.equals("left") ? -1 : 1;

        if ((j == 0 && direction.equals("left")) || (j == 7 && direction.equals("right"))
                || (i == 0 && color.equals("red")) || (i == 7 && color.equals("blue"))) {
            return;
        }

        if (!pannel.squrinfo[i + rowModifier][j + colModifier].blue
                && !pannel.squrinfo[i + rowModifier][j + colModifier].red) {
            pannel.squrinfo[i + rowModifier][j + colModifier].possibleMove = true;
        }

        if (j + (2 * colModifier) < 0 || j + (2 * colModifier) > 7 || i + (2 * rowModifier) < 0
                || i + (2 * rowModifier) > 7) {
            return;
        }

        if (pannel.squrinfo[i + rowModifier][j + colModifier].oppositeColor(color)
                && !pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].blue
                && !pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].red) {
            pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].possibleMove = true;

            if (j + (3 * colModifier) < 0 || j + (3 * colModifier) > 7 || i + (3 * rowModifier) < 0
                    || i + (3 * rowModifier) > 7) {
                return;
            }

            if (pannel.squrinfo[i + (3 * rowModifier)][j + (3 * colModifier)].oppositeColor(color)) {
                kingPattern(i + (2 * rowModifier), j + (2 * colModifier), direction, color);
            }
        }
    }

    private void normalPattern(int i, int j, String direction, String color) {
        int rowModifier = color.equals("blue") ? -1 : 1;
        int colModifier = direction.equals("left") ? 1 : -1;

        if ((j == 7 && direction.equals("left")) || (j == 0 && direction.equals("right"))
                || (i == 7 && color.equals("red")) || (i == 0 && color.equals("blue"))) {
            return;
        }

        if (!pannel.squrinfo[i + rowModifier][j + colModifier].blue
                && !pannel.squrinfo[i + rowModifier][j + colModifier].red) {
            pannel.squrinfo[i + rowModifier][j + colModifier].possibleMove = true;
        }

        if (j + (2 * colModifier) < 0 || j + (2 * colModifier) > 7 || i + (2 * rowModifier) < 0
                || i + (2 * rowModifier) > 7) {
            return;
        }

        if (pannel.squrinfo[i + rowModifier][j + colModifier].oppositeColor(color)
                && !pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].blue
                && !pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].red) {
            pannel.squrinfo[i + (2 * rowModifier)][j + (2 * colModifier)].possibleMove = true;

            if (j + (3 * colModifier) < 0 || j + (3 * colModifier) > 7 || i + (3 * rowModifier) < 0
                    || i + (3 * rowModifier) > 7) {
                return;
            }

            if (pannel.squrinfo[i + (3 * rowModifier)][j + (3 * colModifier)].oppositeColor(color)) {
                normalPattern(i + (2 * rowModifier), j + (2 * colModifier), direction, color);
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
