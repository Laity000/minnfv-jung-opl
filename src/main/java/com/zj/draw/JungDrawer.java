package com.zj.draw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;

import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import javax.swing.JPanel;

import com.google.common.base.Function;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

public class JungDrawer<V, E> {

	/*
	 * 标题
	 */
	private String title;
	/*
	 * 拓扑中的jung图结构
	 */
	private Graph<V, E> topoGraph;
	/*
	 * 流解决方案中的jung图结构集合
	 */
	private LinkedHashMap<Integer, ArrayList<Graph<V, E>>> soluGraphMap;
	/*
	 * jung中的布局
	 */
	private CircleLayout<V, E> layout;

	/*
	 * jung中的视图
	 */
	private VisualizationViewer<V, E> vv;
	/*
	 * jung中的鼠标模式
	 */
	private DefaultModalGraphMouse<V, E> graphMouse;

	private final Stroke edgestroke = new BasicStroke(1);
	private final Stroke pathstroke = new BasicStroke(4);

	public JungDrawer(Graph<V, E> graph, LinkedHashMap<Integer, ArrayList<Graph<V, E>>> graphmap) {
		this.topoGraph = graph;
		this.soluGraphMap = graphmap;
		layout = new CircleLayout<V, E>(graph);
		// 初始化视图，指定高度和宽度范围
		vv = new VisualizationViewer<V, E>(layout, new Dimension(600, 400));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void initDraw() {

		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// f.getContentPane().add();
		Container content = f.getContentPane();
		// 首先需要组装Jung面板
		content.add(initJungPanel());

		JPanel controls = new JPanel();
		controls.add(initMouseModePanel());
		controls.add(initScalePanel());
		controls.add(initSolutionPanel());
		content.add(controls, BorderLayout.SOUTH);
		// 显示
		f.pack();
		f.setVisible(true);
	}

	/**
	 * 初始化jung显示组件
	 *
	 * @return
	 */
	public JPanel initJungPanel() {

		// 背景颜色
		vv.setBackground(Color.white);
		// 设置顶点颜色
		vv.getRenderContext().setVertexFillPaintTransformer(
				new PickableVertexPaintTransformer<V>(vv.getPickedVertexState(), Color.red, Color.yellow));
		// 设置边的颜色
		vv.getRenderContext().setEdgeDrawPaintTransformer(
				new PickableEdgePaintTransformer<E>(vv.getPickedEdgeState(), Color.black, Color.cyan));
		// 设置边为直线
		vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(topoGraph));
		// 设置顶点标签内容
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		// 设置顶点标签位置
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		// 设置边标签内容
		//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		// 设置边标签位置
		vv.getRenderContext().getEdgeLabelRenderer().setRotateEdgeLabels(true);

		/**
		 * the regular graph mouse for the normal view
		 */
		graphMouse = new DefaultModalGraphMouse<V, E>();
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		// add my listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		// create a frome to hold the graph
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		return panel;
	}

	/**
	 * 初始化鼠标模式组件
	 *
	 * @return
	 */
	public JPanel initMouseModePanel() {
		JPanel modePanel = new JPanel(new GridLayout(2, 1));
		modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		modePanel.add(graphMouse.getModeComboBox());
		return modePanel;
	}

	/**
	 * 初始化放缩组件
	 *
	 * @return
	 */
	public JPanel initScalePanel() {
		final ScalingControl scaler = new CrossoverScalingControl();
		JButton plus = new JButton(" + ");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton(" - ");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});
		JPanel scalePanel = new JPanel(new GridLayout(0, 1));
		scalePanel.setBorder(BorderFactory.createTitledBorder("Scale"));
		scalePanel.add(plus);
		scalePanel.add(minus);
		return scalePanel;
	}

	public boolean containsEdge(E edge) {
		if (edge == null) {
			return false;
		}
		for (E e : topoGraph.getEdges()) {
			if (edge.equals(e)) {
				return true;
			}
		}
		return false;
	}

	public JPanel initSolutionPanel() {
		final LinkedHashMap<Integer, ArrayList<Graph<V, E>>> finalgraphmap = soluGraphMap;
		// 下拉选择流
		JComboBox<Integer> combobox = new JComboBox<Integer>();
		for (int item : soluGraphMap.keySet()) {
			combobox.addItem(item);
		}

		combobox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				final Integer item = (Integer) e.getItem();
				Function<E, Stroke> edgeStrokeTransformer = new Function<E, Stroke>() {
					public Stroke apply(E e) {
						for (E path : finalgraphmap.get(item).get(0).getEdges()) {
							if (e.equals(path)) {
								return pathstroke;
							}
						}

						return edgestroke;
					}
				};
				vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
				Function<V, Paint> vertexColorTransformer = new Function<V, Paint>() {
					public Paint apply(V v) {
						for (V nf : finalgraphmap.get(item).get(1).getVertices()) {
							if (v.equals(nf)) {
								if (vv.getPickedVertexState().isPicked(v)) {
									return Color.yellow;
								} else {
									return Color.cyan;
								}

							}
						}
						if (vv.getPickedVertexState().isPicked(v)) {
							return Color.yellow;
						} else {
							return Color.red;
						}
					}
				};
				vv.getRenderContext().setVertexFillPaintTransformer(vertexColorTransformer);

				vv.repaint();
			}
		});

		// 全选流
		int size = finalgraphmap.size();
		JCheckBox checkbox = new JCheckBox("Show all " + size + " flows.");
		checkbox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton) e.getSource();
				Function<E, Stroke> edgeStrokeTransformer;
				if (b.isSelected()) {
					edgeStrokeTransformer = new Function<E, Stroke>() {
						public Stroke apply(E e) {
							for (int item : finalgraphmap.keySet()) {
								for (E path : finalgraphmap.get(item).get(0).getEdges()) {
									if (e.equals(path)) {
										return pathstroke;
									}
								}
							}
							return edgestroke;
						}
					};

				} else {
					edgeStrokeTransformer = new Function<E, Stroke>() {
						public Stroke apply(E e) {
							return edgestroke;
						}
					};
				}
				vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
				vv.repaint();

			}
		});

		JPanel flowSoutionPanel = new JPanel(new GridLayout(0, 1));
		flowSoutionPanel.setBorder(BorderFactory.createTitledBorder("Flow Solutions"));
		flowSoutionPanel.add(checkbox);
		flowSoutionPanel.add(combobox);
		return flowSoutionPanel;
	}

}
