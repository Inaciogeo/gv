package com.nexusbr.gv.controller.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Representation;
import br.org.funcate.glue.model.Theme;
import br.org.funcate.glue.model.ThemeType;
import br.org.funcate.glue.model.tree.CustomNode;
import br.org.funcate.glue.tool.PanTool;
import br.org.funcate.glue.view.GlueMessageDialog;
import br.org.funcate.plugin.GluePluginService;

import com.nexusbr.gv.controller.tools.AddLayerTool;
import com.nexusbr.gv.controller.tools.CommitTool;
import com.nexusbr.gv.controller.tools.DeleteTool;
import com.nexusbr.gv.controller.tools.EditPathTool;
import com.nexusbr.gv.controller.tools.FeatureMoveTool;
import com.nexusbr.gv.controller.tools.FeatureSelectTool;
import com.nexusbr.gv.controller.tools.LineCreatorTool;
import com.nexusbr.gv.controller.tools.NetworkCreatorTool;
import com.nexusbr.gv.controller.tools.PointCreatorTool;
import com.nexusbr.gv.controller.tools.PolygonCreatorTool;
import com.nexusbr.gv.controller.tools.RedoTool;
import com.nexusbr.gv.controller.tools.UndoTool;
import com.nexusbr.gv.view.components.ToolbarWindow;

public class EventsForToolbarWindow implements ActionListener {

	private final ToolbarWindow toolbarWnd;

	public EventsForToolbarWindow(ToolbarWindow toolbarWnd) {
		this.toolbarWnd = toolbarWnd;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(toolbarWnd.getBtnAddLayers())) {
			GluePluginService.setCurrentTool(new AddLayerTool());
		} else if (e.getSource().equals(toolbarWnd.getBtnProperties())) {
			if (toolbarWnd.getFramePropertie().isVisible()) {
				toolbarWnd.getFramePropertie().setVisible(false);
			} else {
				toolbarWnd.getFramePropertie().setVisible(true);
			}
		}

		else if (e.getSource().equals(toolbarWnd.getBtnDelete())) {
			GluePluginService.setCurrentTool(new DeleteTool());
			GluePluginService.setCurrentTool(new FeatureSelectTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnEditPath())) {
			GluePluginService.setCurrentTool(new EditPathTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnUndo())) {
			GluePluginService.setCurrentTool(new UndoTool());
			GluePluginService.setCurrentTool(new PanTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnRedo())) {
			GluePluginService.setCurrentTool(new RedoTool());
			GluePluginService.setCurrentTool(new PanTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnInsPoint())) {
			CustomNode nodeCurrentTheme = AppSingleton.getInstance()
					.getTreeState().getCurrentTheme();

			if (nodeCurrentTheme != null) {

				Theme theme = nodeCurrentTheme.getTheme();
				if (theme != null) {
					List<Representation> reps = theme.getReps();
					boolean validRep = false;
					for (Representation representation : reps) {
						if (representation.getId() == Representation.POINT) {
							validRep = true;
						}
					}
					if (validRep == false) {
						GlueMessageDialog
								.show("Por favor, selecione um tema com uma representação do tipo Pontos!",
										null, 3);
						return;
					}
				} else {
					GlueMessageDialog.show("Por favor, selecione um tema!",
							null, 3);
					return;
				}

			} else {
				GlueMessageDialog
						.show("Por favor, selecione um tema!", null, 3);
				return;
			}

			GluePluginService.setCurrentTool(new PointCreatorTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnDLine())) {
			CustomNode nodeCurrentTheme = AppSingleton.getInstance()
					.getTreeState().getCurrentTheme();

			if (nodeCurrentTheme != null) {

				Theme theme = nodeCurrentTheme.getTheme();
				if (theme != null) {
					List<Representation> reps = theme.getReps();
					boolean validRep = false;
					for (Representation representation : reps) {
						if (representation.getId() == Representation.LINE) {
							validRep = true;
						}
					}
					if (validRep == false) {
						GlueMessageDialog
								.show("Por favor, selecione um tema com uma representação do tipo Linhas!",
										null, 3);
						return;
					}
				} else {
					GlueMessageDialog.show("Por favor, selecione um tema!",
							null, 3);
					return;
				}

			} else {
				GlueMessageDialog
						.show("Por favor, selecione um tema!", null, 3);
				return;
			}

			GluePluginService.setCurrentTool(new LineCreatorTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnDNetwork())) {
			boolean validTheme = false;
			CustomNode node = AppSingleton.getInstance().getMediator()
					.getCurrentTheme();
			if (node != null) {
				Theme theme = node.getTheme();
				if (theme != null
						&& theme.getType() == ThemeType.NETWORK_THEME
								.getNumber()) {
					GluePluginService.setCurrentTool(new NetworkCreatorTool());
					validTheme = true;
				}
			}
			if (validTheme == false) {
				GlueMessageDialog.show(
						"Por favor, selecione um tema do tipo Rede!", null, 3);
				return;
			}
		}

		else if (e.getSource().equals(toolbarWnd.getBtnSelectGeom())) {
			GluePluginService.setCurrentTool(new FeatureSelectTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnMoveGeom())) {
			GluePluginService.setCurrentTool(new FeatureMoveTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnDPoly())) {

			CustomNode nodeCurrentTheme = AppSingleton.getInstance()
					.getTreeState().getCurrentTheme();

			if (nodeCurrentTheme != null) {

				Theme theme = nodeCurrentTheme.getTheme();
				if (theme != null) {
					List<Representation> reps = theme.getReps();
					boolean validRep = false;
					for (Representation representation : reps) {
						if (representation.getId() == Representation.POLYGON) {
							validRep = true;
						}
					}
					if (validRep == false) {
						GlueMessageDialog
								.show("Por favor, selecione um tema com uma representação do tipo Poligono!",
										null, 3);
						return;
					}
				} else {
					GlueMessageDialog.show("Por favor, selecione um tema!",
							null, 3);
					return;
				}

			} else {
				GlueMessageDialog
						.show("Por favor, selecione um tema!", null, 3);
				return;
			}

			GluePluginService.setCurrentTool(new PolygonCreatorTool());
		}

		else if (e.getSource().equals(toolbarWnd.getBtnSave())) {
			GluePluginService.setCurrentTool(new CommitTool());
		}
	}

}