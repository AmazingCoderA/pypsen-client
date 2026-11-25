package pypsen.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.text.Text;

/** Minimal DrawContext facade over the 26.3 GuiGraphicsExtractor. */
public final class DrawContext26 {
    private final GuiGraphicsExtractor extractor;

    public DrawContext26(GuiGraphicsExtractor extractor) {
        this.extractor = extractor;
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        extractor.fill(x1, y1, x2, y2, color);
    }

    public void drawTextWithShadow(TextRenderer renderer, Text text, int x, int y, int color) {
        extractor.text(renderer, text, x, y, color, true);
    }

    public void drawTextWithShadow(TextRenderer renderer, String text, int x, int y, int color) {
        extractor.text(renderer, text, x, y, color, true);
    }

    public void drawCenteredTextWithShadow(TextRenderer renderer, Text text, int x, int y, int color) {
        extractor.centeredText(renderer, text, x, y, color);
    }

    public void drawCenteredTextWithShadow(TextRenderer renderer, String text, int x, int y, int color) {
        extractor.centeredText(renderer, text, x, y, color);
    }

    public int getScaledWidth() {
        return extractor.guiWidth();
    }

    public int getScaledHeight() {
        return extractor.guiHeight();
    }
}
