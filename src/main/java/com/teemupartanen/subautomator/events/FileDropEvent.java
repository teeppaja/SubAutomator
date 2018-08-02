package com.teemupartanen.subautomator.events;

import com.teemupartanen.subautomator.services.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class FileDropEvent extends DropTarget {
    private static final Logger logger = LogManager.getLogger(FileDropEvent.class);

    @SuppressWarnings("unchecked") //DataFlavor guarantees objects to be Files
    @Override
    public synchronized void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY);
        try {
            List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            logger.debug("Files imported: " + droppedFiles.size());
            EventService.filesDroppedEvent(droppedFiles.toArray(new File[0]));
            if (this.getComponent() instanceof JTextComponent) {
                JTextComponent target = (JTextComponent) this.getComponent();
                target.setText(EventService.getDroppedFilesAsString());
                Container window = target.getTopLevelAncestor();
                window.setSize(Math.max(600, window.getPreferredSize().width + 10),
                        Math.max(150, window.getPreferredSize().height + 10));
            }
        } catch (UnsupportedFlavorException e) {
            logger.debug("Dropped content was not file: ", e);
        } catch (Exception e) {
            logger.error("FAIL: ", e);
        }
    }
}
