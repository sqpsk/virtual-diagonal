package siggui.rangecontrols;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import siggui.utility.SigMath;
import zplot.utility.IntervalTransform;

public class RangeSlider extends JComponent {

    public RangeSlider() {
        addMouseListener(trackListener);
        addMouseMotionListener(trackListener);
        setPreferredSize(new Dimension(10, 10));
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && !isEnabled()) {
            addMouseListener(trackListener);
            addMouseMotionListener(trackListener);
        } else if (!enabled && isEnabled()) {
            removeMouseListener(trackListener);
            removeMouseMotionListener(trackListener);
        }
        super.setEnabled(enabled);
    }

    public boolean isAdjusting() {
        return model.isAdjusting();
    }

    public IRangeModel getModel() {
        return model;
    }

    public void setModel(IRangeModel model) {
        if (this.model != null) {
            removeMouseListener(trackListener);
            removeMouseMotionListener(trackListener);
            this.model.removePropertyChangeListener(modelChangeListener);
        }

        this.model = model;

        if (this.model != null) {
            addMouseListener(trackListener);
            addMouseMotionListener(trackListener);
            this.model.addPropertyChangeListener(modelChangeListener);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Rectangle vr = getVisibleRect();
        paintComponent(g2, vr.width, vr.height);
    }

    private void paintComponent(Graphics2D g, int width, int height) {
        g.setColor(isEnabled() ? TRACK_COLOR : TRACK_COLOR_DIS);
        int minX = HANDLE_WIDTH;
        int maxX = width - 1 - HANDLE_WIDTH;
        int minY = 0;
        int maxY = height - 1;

        // Left vertical line
        g.drawLine(minX, minY, minX, maxY);

        // Right vertical line
        g.drawLine(maxX, minY, maxX, maxY);

        // Draw horizontal track
        int centerY = height / 2;
        g.drawLine(minX, centerY, maxX, centerY);

        // Draw knob with handles
        IntervalTransform t = new IntervalTransform(0, model.getFileSize(), HANDLE_WIDTH, width - 1 - HANDLE_WIDTH);
        int knobBegin = (int) t.transform(model.getBegin());
        int knobEnd = SigMath.roundPositive(t.transform(model.getEnd()));
        int knobSize = knobEnd - knobBegin;

        g.setColor(isEnabled() ? KNOB_BORDER_COLOR : KNOB_BORDER_COLOR_DIS);
        g.drawRect(knobBegin - HANDLE_WIDTH, minY, knobSize + 2 * HANDLE_WIDTH, height - 1);
        g.drawLine(knobBegin, minY, knobBegin, maxY);
        g.drawLine(knobEnd, minY, knobEnd, maxY);

        g.setColor(isEnabled() ? KNOB_REGION_COLOR : KNOB_REGION_COLOR_DIS);
        g.fillRect(knobBegin + 1, minY + 1, knobSize - 1, height - 2);

        g.setColor(isEnabled() ? KNOB_HANDLE_COLOR : KNOB_HANDLE_COLOR_DIS);
        g.fillRect(knobBegin - HANDLE_WIDTH + 1, minY + 1, HANDLE_WIDTH - 1, height - 2);
        g.fillRect(knobEnd + 1, minY + 1, HANDLE_WIDTH - 1, height - 2);
    }

    private IntervalTransform modelToSliderT() {
        return new IntervalTransform(0, model.getFileSize(), HANDLE_WIDTH, getVisibleRect().width - 1 - HANDLE_WIDTH);
    }

    private IntervalTransform sliderToModelT() {
        return new IntervalTransform(HANDLE_WIDTH, getVisibleRect().width - 1 - HANDLE_WIDTH, 0, model.getFileSize());
    }
    private final PropertyChangeListener modelChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            repaint();
        }
    };
    private final MouseInputListener trackListener = new MouseInputListener() {
        @Override
        public void mouseClicked(MouseEvent me) {
            model.setIsAdjusting(false);
            if (mode == SliderMode.NONE) {
                IntervalTransform sliderToModelT = sliderToModelT();
                long begin = SigMath.roundPositive(sliderToModelT.transform(me.getX()));
                model.setBeginSameSizePriorityBegin(begin);
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
            model.setIsAdjusting(false);
            dragBegin = me.getPoint();
            dragStartModelBegin = model.getBegin();
            dragStartModelEnd = model.getEnd();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            model.setIsAdjusting(false);
            mouseDraggedImpl(me);
            dragBegin = null;
            dragStartModelBegin = -1;
            dragStartModelEnd = -1;
            setMode(SliderMode.NONE, Cursor.DEFAULT_CURSOR);
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (dragBegin != null && mode != SliderMode.NONE) {
                model.setIsAdjusting(true);
            }
            mouseDraggedImpl(me);
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            model.setIsAdjusting(false);
            IntervalTransform modelToSliderT = modelToSliderT();
            int knobBegin = (int) modelToSliderT.transform(model.getBegin());
            int knobEnd = SigMath.roundPositive(modelToSliderT.transform(model.getEnd()));

            final int x = me.getX();
            boolean resizeLeft = intervalContains(knobBegin - HANDLE_WIDTH, x, knobBegin);
            boolean resizeRight = intervalContains(knobEnd, x, knobEnd + HANDLE_WIDTH);
            boolean move = intervalContains(knobBegin, x, knobEnd);

            if (resizeLeft) {
                setMode(SliderMode.RESIZE_LEFT, Cursor.W_RESIZE_CURSOR);
            } else if (resizeRight) {
                setMode(SliderMode.RESIZE_RIGHT, Cursor.E_RESIZE_CURSOR);
            } else if (move) {
                setMode(SliderMode.MOVE, Cursor.DEFAULT_CURSOR);
            } else {
                setMode(SliderMode.NONE, Cursor.DEFAULT_CURSOR);
            }
        }

        private void mouseDraggedImpl(MouseEvent me) {
            if (dragBegin != null) {
                if (mode == SliderMode.RESIZE_LEFT) {
                    setBegin(me);
                } else if (mode == SliderMode.RESIZE_RIGHT) {
                    setEnd(me);
                } else if (mode == SliderMode.MOVE) {
                    moveBegin(me);
                }
            }
        }

        private void setMode(SliderMode mode, int cursorType) {
            if (this.mode != mode) {
                this.mode = mode;
                setCursor(new Cursor(cursorType));
            }
        }

        private long calculateMoveModelDelta(MouseEvent me) {
            IntervalTransform sliderToModelT = sliderToModelT();
            long modelX = SigMath.roundPositive(sliderToModelT.transform(me.getX()));
            long modelDragBegin = SigMath.roundPositive(sliderToModelT.transform(dragBegin.x));
            return modelX - modelDragBegin;
        }

        private void moveBegin(MouseEvent me) {
            long begin = dragStartModelBegin + calculateMoveModelDelta(me);
            model.setBeginSameSizePriorityBegin(begin);
        }

        // Change the size of the region - keep end fixed
        // We do not allow begin to move beyond the current begin.
        private void setBegin(MouseEvent me) {
            long begin = dragStartModelBegin + calculateMoveModelDelta(me);
            model.setBeginSameEndPriorityEnd(begin);
        }

        private void setEnd(MouseEvent me) {
            long end = dragStartModelEnd + calculateMoveModelDelta(me);
            model.setEndSameBeginPriorityBegin(end);
        }
        private SliderMode mode = SliderMode.NONE;
        private Point dragBegin = null;
        private long dragStartModelBegin = 0;
        private long dragStartModelEnd = 0;
    };

    private static boolean intervalContains(int begin, int x, int end) {
        assert begin <= end;
        return begin <= x && x < end;
    }

    private static enum SliderMode {

        RESIZE_LEFT, RESIZE_RIGHT, MOVE, NONE
    };
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Color KNOB_BORDER_COLOR = Color.GRAY;
    private static final Color KNOB_REGION_COLOR = new Color(255, 102, 102);
    private static final Color KNOB_HANDLE_COLOR = new Color(218, 218, 255);
    private static final Color TRACK_COLOR_DIS = Color.GRAY;
    private static final Color KNOB_BORDER_COLOR_DIS = KNOB_BORDER_COLOR.brighter();
    private static final Color KNOB_REGION_COLOR_DIS = KNOB_REGION_COLOR.brighter();
    private static final Color KNOB_HANDLE_COLOR_DIS = KNOB_HANDLE_COLOR.brighter();
    private static final int HANDLE_WIDTH = 5;
    private IRangeModel model = new BoundedRangeModel(1024, 1, 1, 0, 1);
}
