package zplot.utility;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public abstract class SaveImageAction implements ActionListener {

    public SaveImageAction(Component component) {
        this.component = component;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save image");
        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new File(makePngFilename()));
        if (fc.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
            doSave(fc.getSelectedFile());
        }
    }

    private void doSave(File file) {
        BufferedImage im = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
        component.paint(im.getGraphics());
        try {
            ImageIO.write(im, "PNG", file);
        } catch (IOException ex) {
            System.out.println("SaveImageAction: ERROR: Faild to save image: " + ex);
        }
    }
    
    protected abstract String makePngFilename();
    
    private final Component component;
}