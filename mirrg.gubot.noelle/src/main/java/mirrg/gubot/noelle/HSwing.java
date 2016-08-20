package mirrg.gubot.noelle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import jp.hishidama.swing.layout.GroupLayoutUtil;

public class HSwing
{

	public static Component createHorizontalSplitPane(Component... components)
	{
		return createHorizontalSplitPane(Arrays.asList(components));
	}

	public static Component createHorizontalSplitPane(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
			components.get(0),
			createHorizontalSplitPane(components.subList(1, components.size())));
	}

	public static Component createVerticalSplitPane(Component... components)
	{
		return createVerticalSplitPane(Arrays.asList(components));
	}

	public static Component createVerticalSplitPane(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
			components.get(0),
			createVerticalSplitPane(components.subList(1, components.size())));
	}

	public static Component createBorderPanelUp(Component... components)
	{
		return createBorderPanelUp(Arrays.asList(components));
	}

	public static Component createBorderPanelUp(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.NORTH);
		panel.add(createBorderPanelUp(components.subList(1, components.size())), BorderLayout.CENTER);

		return panel;
	}

	public static Component createBorderPanelDown(Component... components)
	{
		return createBorderPanelDown(Arrays.asList(components));
	}

	public static Component createBorderPanelDown(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(createBorderPanelDown(components.subList(0, components.size() - 1)), BorderLayout.CENTER);
		panel.add(components.get(components.size() - 1), BorderLayout.SOUTH);

		return panel;
	}

	public static Component createBorderPanelLeft(Component... components)
	{
		return createBorderPanelLeft(Arrays.asList(components));
	}

	public static Component createBorderPanelLeft(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.WEST);
		panel.add(createBorderPanelLeft(components.subList(1, components.size())), BorderLayout.CENTER);

		return panel;
	}

	public static Component createBorderPanelRight(Component... components)
	{
		return createBorderPanelRight(Arrays.asList(components));
	}

	public static Component createBorderPanelRight(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(createBorderPanelRight(components.subList(0, components.size() - 1)), BorderLayout.CENTER);
		panel.add(components.get(components.size() - 1), BorderLayout.EAST);

		return panel;
	}

	public static JPanel createGridPanel(Component[][] grid)
	{
		JPanel panel = new JPanel();

		GroupLayoutUtil g = new GroupLayoutUtil();
		g.setComponents(grid);
		g.setGroupLayoutTo(panel);

		return panel;
	}

	public static JPanel createVerticalBorderPanel(Component top, Component middle, Component bottom)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (top != null) panel.add(top, BorderLayout.NORTH);
		if (middle != null) panel.add(middle, BorderLayout.CENTER);
		if (bottom != null) panel.add(bottom, BorderLayout.SOUTH);

		return panel;
	}

	public static JPanel createHorizontalBorderPanel(Component left, Component center, Component right)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (left != null) panel.add(left, BorderLayout.WEST);
		if (center != null) panel.add(center, BorderLayout.CENTER);
		if (right != null) panel.add(right, BorderLayout.EAST);

		return panel;
	}

	public static <T> T process(T object, Consumer<T> consumer)
	{
		consumer.accept(object);
		return object;
	}

	public static <T> T get(Supplier<T> supplier)
	{
		return supplier.get();
	}

	public static void setPreferredSize(JLabel label, int width, int rows)
	{
		label.setPreferredSize(new Dimension(width, label.getFont().getSize() * rows + 6));
	}

	public static JPanel createPanel(Consumer<JPanel> initializer)
	{
		JPanel panel = new JPanel();
		initializer.accept(panel);
		return panel;
	}

	public static JPanel createPanel(Component... components)
	{
		JPanel panel = new JPanel();
		Stream.of(components)
			.forEach(panel::add);
		return panel;
	}

	public static JButton createButton(String caption, ActionListener listener)
	{
		JButton button = new JButton(caption);
		button.addActionListener(listener);
		return button;
	}

	public static JScrollPane createScrollPane(Component component)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		return scrollPane;
	}

	public static JScrollPane createScrollPane(Component component, int width, int height)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

}
