package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.OakTreeGUI;

public class PagePanelControl extends PanelControl<PagePanelControl> {
    public int page = 0;

    public PagePanelControl() {
        this.id = "page_panel_control";
    }

    public PagePanelControl page(int page) {
        if (page < 0) this.page = 0;
        else if (page >= children.size()) this.page = children.size() - 1;
        else this.page = page;

        return this;
    }

    public PagePanelControl nextPage() {
        page(page + 1);
        return this;
    }

    public PagePanelControl previousPage() {
        page(page - 1);
        return this;
    }

    public PagePanelControl flipPages(int count) {
        page(page + count);
        return this;
    }

    @Override
    void arrangeChildren(int mouseX, int mouseY, float deltaTime, OakTreeGUI gui) {
        Control child = children.get(page);
        if (child != null) child.preDraw(mouseX, mouseY, deltaTime, gui, innerX, innerY, innerWidth, innerHeight);
    }

    @Override
    boolean shouldDraw(Control child) {
        return children.indexOf(child) == page;
    }
}
