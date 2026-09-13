package me.scarletleaf1000.slagtraits.client;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.items.modular.DataDynamicParts;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.integration.EquipmentClassifier;
import me.scarletleaf1000.slagtraits.recipe.smithing.ModifierService;
import me.scarletleaf1000.slagtraits.traits.ActiveTrait;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.data.AppliedModifiers;
import me.scarletleaf1000.slagtraits.traits.data.ModDataComponents;
import me.scarletleaf1000.slagtraits.traits.leveling.ToolLeveling;
import me.scarletleaf1000.slagtraits.traits.resolver.TraitResolver;
import me.scarletleaf1000.slagtraits.util.DisplayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SmithingInfoPanel extends AbstractWidget {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "textures/gui/smithing_info_background.png");

    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEIGHT = 160;
    private static final int GAP = 4;
    private static final int MARGIN = 6;
    private static final int LINE_HEIGHT = 10;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int SCROLLBAR_PAD = 3;
    private static final int BASE_SLOT = 1;
    private static final int TOOLTIP_WRAP_WIDTH = 170;

    private static final int TEXT_COLOR = 0xFFDDDDDD;
    private static final int LABEL_COLOR = 0xFFAAAAAA;
    private static final int HEADER_COLOR = 0xFFFFFFFF;
    private static final int TRACK_COLOR = 0xFF1A1A1A;
    private static final int HANDLE_COLOR = 0xFF777777;
    private static final int HANDLE_ACTIVE_COLOR = 0xFF999999;

    private final SmithingMenu menu;
    private final Font font;

    private int wrapWidth;
    private int scrollOffset;
    private int contentHeight;
    private boolean draggingScrollbar;
    private int grabOffset;

    public SmithingInfoPanel(SmithingScreen screen) {
        super(Math.max(0, screen.getGuiLeft() - PANEL_WIDTH - GAP),
                screen.getGuiTop(), PANEL_WIDTH, PANEL_HEIGHT, Component.empty());
        this.menu = screen.getMenu();
        this.font = Minecraft.getInstance().font;
    }

    private record Line(List<FormattedCharSequence> text, int color, boolean centered,
                        List<FormattedCharSequence> tooltip) {
        int height() {
            return Math.max(1, text.size()) * LINE_HEIGHT;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(BACKGROUND, getX(), getY(), 0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT);

        ItemStack stack = menu.slots.size() > BASE_SLOT ? menu.getSlot(BASE_SLOT).getItem() : ItemStack.EMPTY;
        int contentTop = getY() + MARGIN;
        int contentBottom = contentTop + visibleHeight();
        int contentRight = getX() + PANEL_WIDTH - MARGIN;
        int contentX = getX() + MARGIN;

        List<Line> lines = buildLines(stack, contentRight - contentX);
        contentHeight = lines.stream().mapToInt(Line::height).sum();
        boolean scrollable = contentHeight > visibleHeight();
        if (scrollable) {
            contentX += SCROLLBAR_WIDTH + 2;
            lines = buildLines(stack, contentRight - contentX);
            contentHeight = lines.stream().mapToInt(Line::height).sum();
        }
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll());

        graphics.enableScissor(contentX, contentTop, contentRight, contentBottom);
        int y = contentTop - scrollOffset;
        List<FormattedCharSequence> hoveredTooltip = List.of();
        for (Line line : lines) {
            int lineTop = y;
            for (FormattedCharSequence text : line.text()) {
                int drawX = line.centered()
                        ? contentX + (contentRight - contentX - font.width(text)) / 2
                        : contentX;
                graphics.drawString(font, text, drawX, y, line.color());
                y += LINE_HEIGHT;
            }
            if (line.text().isEmpty()) y += LINE_HEIGHT;
            if (!line.tooltip().isEmpty()
                    && mouseX >= contentX && mouseX < contentRight
                    && mouseY >= Math.max(lineTop, contentTop)
                    && mouseY < Math.min(y, contentBottom)) {
                hoveredTooltip = line.tooltip();
            }
        }
        graphics.disableScissor();

        if (scrollable) renderScrollbar(graphics, mouseX, mouseY);
        if (!hoveredTooltip.isEmpty()) graphics.renderTooltip(font, hoveredTooltip, mouseX, mouseY);
    }

    private List<Line> buildLines(ItemStack stack, int wrapWidth) {
        this.wrapWidth = wrapWidth;
        List<Line> lines = new ArrayList<>();
        if (stack.isEmpty()) {
            addLine(lines, Component.translatable("gui.slagtraits.empty"), LABEL_COLOR, true);
            return lines;
        }

        List<ActiveTrait> traits = TraitResolver.getActiveTraits(stack).stream()
                .filter(t -> t.trait() != null && !t.trait().isHidden())
                .toList();
        AppliedModifiers modifiers = stack.getOrDefault(ModDataComponents.MODIFIERS, AppliedModifiers.EMPTY);

        if (ToolLeveling.isLevelable(stack)) {
            int level = ToolLeveling.getLevel(stack);
            int used = ModifierService.slotsUsed(modifiers);
            int total = ToolLeveling.getModifierSlots(stack);
            MutableComponent levelLine = Component.translatable("tooltip.slagtraits.level",
                            DisplayUtils.intToRoman(level))
                    .withStyle(ChatFormatting.YELLOW);
            addLine(lines, levelLine, TEXT_COLOR, false);
            MutableComponent slotsLine = Component.translatable("gui.slagtraits.modifier_slots", used, total)
                    .withStyle(ChatFormatting.GRAY);
            addLine(lines, slotsLine, TEXT_COLOR, false);
        }

        if (!traits.isEmpty()) {
            addHeader(lines, "gui.slagtraits.traits");
            for (ActiveTrait active : traits) {
                Trait trait = active.trait();
                int displayTier = active.tier();
                String progress = "";
                if (trait.isModifier() && modifiers.has(trait.getId()) && active.tier() < trait.getMaxTier()) {
                    displayTier = active.tier() + 1;
                    progress = " (" + modifiers.getValue(trait.getId())
                            + "/" + ModifierService.valueForTier(trait, displayTier) + ")";
                }
                MutableComponent name = Component.literal(
                        trait.getDisplayName() + " " + DisplayUtils.intToRoman(displayTier) + progress);
                lines.add(new Line(font.split(name, wrapWidth), 0xFF000000 | trait.getColor(), false,
                        traitTooltip(name, trait)));
            }
        }

        if (stack.getItem() instanceof IModularItem modular) {
            List<Line> stats = new ArrayList<>();
            addStat(stats, "attack_damage", modular.getSharp(stack));
            addStat(stats, "mining_speed", modular.getSpeed(stack));
            addStat(stats, "attack_speed", modular.getAttackSpeed(stack));
            addStat(stats, "durability", modular.getDura(stack));
            addStat(stats, "tier", modular.getTier(stack));
            addStat(stats, "defense", modular.getDefense(stack));
            addStat(stats, "toughness", modular.getTough(stack));
            addStat(stats, "kb_resist", modular.getKbRes(stack));
            addStat(stats, "enchantability", modular.getEnch(stack));
            if (!stats.isEmpty() && EquipmentClassifier.getEquipmentType(stack) == EquipmentType.TOOL) {
                addHeader(lines, "gui.slagtraits.stats");
                lines.addAll(stats);
            }

            DataDynamicParts parts = modular.getParts(stack);
            if (parts != null && !parts.isEmpty()) {
                addHeader(lines, "gui.slagtraits.parts");
                for (ItemStack part : parts.itemsCopy()) {
                    addLine(lines, part.getHoverName().copy().withStyle(ChatFormatting.GRAY), TEXT_COLOR, false);
                }
            }
        }


        if (lines.isEmpty()) {
            addLine(lines, Component.translatable("gui.slagtraits.no_info"), LABEL_COLOR, true);
        }
        return lines;
    }

    private List<FormattedCharSequence> traitTooltip(MutableComponent name, Trait trait) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(name.copy().withStyle(style -> style.withColor(trait.getColor())).getVisualOrderText());
        String description = trait.getDescription();
        if (description != null && !description.isEmpty()) {
            tooltip.addAll(font.split(Component.literal(description).withStyle(ChatFormatting.GRAY), TOOLTIP_WRAP_WIDTH));
        }
        return tooltip;
    }

    private void addLine(List<Line> lines, Component text, int color, boolean centered) {
        lines.add(new Line(font.split(text, wrapWidth), color, centered, List.of()));
    }

    private void addHeader(List<Line> lines, String key) {
        if (!lines.isEmpty()) lines.add(new Line(List.of(), 0, false, List.of()));
        addLine(lines, Component.translatable(key).withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE), HEADER_COLOR, true);
    }

    private void addStat(List<Line> lines, String key, float value) {
        if (value == 0f) return;
        MutableComponent label = Component.translatable("gui.slagtraits.stat." + key).withStyle(ChatFormatting.GRAY);
        addLine(lines, label.append(Component.literal(": " + formatStat(value)).withStyle(ChatFormatting.WHITE)), TEXT_COLOR, false);
    }

    private static String formatStat(float v) {
        if (v == Math.floor(v) && Float.isFinite(v)) return Integer.toString((int) v);
        return String.format(Locale.ROOT, "%.1f", v);
    }

    // --- Scrolling ---

    private int visibleHeight() {
        return PANEL_HEIGHT - MARGIN * 2;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight - visibleHeight());
    }

    private int scrollbarHandleHeight() {
        return Math.max(12, visibleHeight() * visibleHeight() / Math.max(1, contentHeight));
    }

    private int scrollbarHandleY() {
        int trackY = getY() + MARGIN;
        int range = visibleHeight() - scrollbarHandleHeight();
        int max = maxScroll();
        return range <= 0 || max <= 0 ? trackY : trackY + range * scrollOffset / max;
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int trackX = getX() + SCROLLBAR_PAD;
        int trackY = getY() + MARGIN;
        return mouseX >= trackX && mouseX < trackX + SCROLLBAR_WIDTH
                && mouseY >= trackY && mouseY < trackY + visibleHeight();
    }

    private void scrollToHandle(int handleY) {
        int range = visibleHeight() - scrollbarHandleHeight();
        scrollOffset = range <= 0 ? 0
                : Mth.clamp((handleY - (getY() + MARGIN)) * maxScroll() / range, 0, maxScroll());
    }

    private void renderScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
        int trackX = getX() + SCROLLBAR_PAD;
        int trackY = getY() + MARGIN;
        int handleY = scrollbarHandleY();
        int handleH = scrollbarHandleHeight();
        boolean hover = mouseX >= trackX && mouseX < trackX + SCROLLBAR_WIDTH
                && mouseY >= handleY && mouseY < handleY + handleH;
        graphics.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + visibleHeight(), TRACK_COLOR);
        graphics.fill(trackX, handleY, trackX + SCROLLBAR_WIDTH, handleY + handleH,
                hover || draggingScrollbar ? HANDLE_ACTIVE_COLOR : HANDLE_COLOR);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY) || maxScroll() <= 0) return false;
        scrollOffset = Mth.clamp(scrollOffset - (int) (scrollY * LINE_HEIGHT), 0, maxScroll());
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible || !isValidClickButton(button) || !isMouseOver(mouseX, mouseY)) return false;
        if (maxScroll() > 0 && isOverScrollbar(mouseX, mouseY)) {
            draggingScrollbar = true;
            int handleY = scrollbarHandleY();
            int handleH = scrollbarHandleHeight();
            if (mouseY >= handleY && mouseY < handleY + handleH) {
                grabOffset = (int) mouseY - handleY;
            } else {
                grabOffset = handleH / 2;
                scrollToHandle((int) mouseY - grabOffset);
            }
        }
        return true; // consume clicks inside the panel without playing a click sound
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar && isValidClickButton(button)) {
            scrollToHandle((int) mouseY - grabOffset);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingScrollbar) {
            draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.slagtraits.panel"));
    }
}
