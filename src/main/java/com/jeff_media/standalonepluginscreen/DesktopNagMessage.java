package com.jeff_media.standalonepluginscreen;

import com.allatori.annotations.StringEncryption;
import com.jeff_media.standalonepluginscreen.NagMessage;
import com.jeff_media.standalonepluginscreen.StandalonePluginScreen;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

@StringEncryption(value="disable")
public final class DesktopNagMessage
extends NagMessage {
    @Override
    public void show() {
        JLabel jLabel = new JLabel();
        Font font = jLabel.getFont();
        String string2 = String.format("font-family:%s;font-weight:%s;font-size:%dpt;", font.getFamily(), font.isBold() ? "bold" : "normal", font.getSize());
        JEditorPane jEditorPane = new JEditorPane("text/html", "<html><body style=\"" + string2 + "\">" + DesktopNagMessage.getMessage().stream().map(string -> string.replace("{spigotLink}", DesktopNagMessage.hyperlink(DesktopNagMessage.getSetupSpigotLink(), DesktopNagMessage.getSetupSpigotLink())).replace("{discordLink}", DesktopNagMessage.hyperlink(DesktopNagMessage.getDiscordLink(), DesktopNagMessage.getDiscordLink()))).collect(Collectors.joining(System.lineSeparator() + "<br/>")) + "</body></html>");
        jEditorPane.addHyperlinkListener(hyperlinkEvent -> {
            if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                }
                catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
        });
        jEditorPane.setEditable(false);
        jEditorPane.setBackground(jLabel.getBackground());
        jEditorPane.setFocusable(false);
        JFrame jFrame = new JFrame(DesktopNagMessage.getTitle());
        jFrame.setUndecorated(true);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setIconImages(DesktopNagMessage.getIcons());
        JOptionPane.showMessageDialog(jFrame, jEditorPane, DesktopNagMessage.getTitle(), 0);
        jFrame.dispose();
    }

    private static String hyperlink(String string, String string2) {
        return "<a href=\"" + string2 + "\">" + string + "</a>";
    }

    private static ArrayList<Image> getIcons() {
        ArrayList<Image> arrayList = new ArrayList<Image>();
        for (String string : new String[]{"logo_32_32.png", "logo_64_64.png"}) {
            try {
                BufferedImage bufferedImage = ImageIO.read(StandalonePluginScreen.class.getResourceAsStream("/" + string));
                arrayList.add(bufferedImage);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return arrayList;
    }
}

