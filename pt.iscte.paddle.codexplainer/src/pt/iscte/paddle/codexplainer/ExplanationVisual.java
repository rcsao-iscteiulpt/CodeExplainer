package pt.iscte.paddle.codexplainer;

import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.javardise.ClassWidget;
import pt.iscte.paddle.javardise.Constants;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.MethodWidget;
import pt.iscte.paddle.javardise.MarkerService.Mark;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IBlock.IVisitor;
import pt.iscte.paddle.model.demo2.MostWantedHolder;
import pt.iscte.paddle.model.demo2.VariableRoleExplainer;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.codexplainer.temp.*;

public class ExplanationVisual {

	private static Shell shell;

	static Map<IVariableDeclaration, String> variablesRolesExplanation = new HashMap<IVariableDeclaration, String>();

	private static void getVariableRole(IVariableDeclaration var) {

		if (MostWantedHolder.isMostWantedHolder(var)) {
			System.out.println(var);
			IVariableRole role = new MostWantedHolder(var);
			variablesRolesExplanation.put(var, VariableRoleExplainer.getMostWantedHolderExplanation(role));
		}

//		if(IGatherer.isGatherer(var)) {
//			return IGatherer.createGatherer(var);
//		}
//      etc....
		return;
	}

	public static void main(String[] args) {

		TestMaxArray t = new TestMaxArray();
		t.setup();
		IModule module = t.getModule();
		IProcedure proc = module.getProcedure("max");
		
		
		for(IVariableDeclaration var : proc.getVariables()) {
			//System.out.println(var);	
			getVariableRole(var);
		}
		

		
		//Visual
		Display display = new Display();
		shell = new Shell(display);
		shell.setBackground(Constants.COLOR_BACKGROUND);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 50;
		layout.marginLeft = 50;
		layout.verticalSpacing = 20;
	
		shell.setLayout(layout);

		Composite comp = new Composite(shell, SWT.BORDER);
		RowLayout rLayout = new RowLayout();
		comp.setLayout(rLayout);
		
		ClassWidget widget = new ClassWidget(comp, module);
		// module.getProcedures().forEach(p -> body.addElement(comp -> new
		// MethodWidget(comp, p), p));
		widget.setEnabled(false);

		System.out.println(proc.getBody().getChildren());
		List<IBlockElement> children = proc.getBody().getChildren();

		Composite compButtons = new Composite(shell, SWT.BORDER);
		compButtons.setLayout(new FillLayout());

		Button next = new Button(compButtons, SWT.PUSH);
		next.setText("next");
		next.addSelectionListener(new SelectionAdapter() {
			Color red = new Color(display, 255, 0, 0);
			Iterator<IBlockElement> it = children.iterator();
			IBlockElement element = it.next();
			Mark mark;

			public void widgetSelected(SelectionEvent e) {
				if (mark != null)
					mark.unmark();

				mark = MarkerService.mark(red, element);
				if (it.hasNext())
					element = it.next();
			}
		});

		// --------------

		Image arrow = new Image(display, "arrow.png");
		List<Decoration> decAssignments = new ArrayList<>();

		Button markAssignment = new Button(compButtons, SWT.PUSH);
		markAssignment.setText("mark assignments");
		markAssignment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (decAssignments.isEmpty()) {
					proc.accept(new IVisitor() {
						public boolean visit(IVariableAssignment a) {
							Decoration d = MarkerService.addDecoration(a, arrow, Decoration.Location.LEFT);
							decAssignments.add(d);
							return true;
						}
					});
				}
				decAssignments.forEach(d -> d.show());
			}
		});

		Button removeMarkAssignment = new Button(compButtons, SWT.PUSH);
		removeMarkAssignment.setText("remove mark assignments");
		removeMarkAssignment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				decAssignments.forEach(d -> d.hide());
			}
		});

		// --------------

		List<Decoration> returnMarks = new ArrayList<>();

		Button markReturn = new Button(compButtons, SWT.PUSH);
		markReturn.setText("mark return");
		markReturn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (returnMarks.isEmpty()) {
					proc.accept(new IVisitor() {
						public boolean visit(IReturn r) {
							Decoration d = MarkerService.addDecoration(r, "this is the end", Decoration.Location.RIGHT);
							returnMarks.add(d);
							return true;
						}
					});
				}
				returnMarks.forEach(d -> d.show());
			}
		});

		Button removeReturnMarks = new Button(compButtons, SWT.PUSH);
		removeReturnMarks.setText("delete return marks");
		removeReturnMarks.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				returnMarks.forEach(d -> d.delete());
				returnMarks.clear();
			}
		});

		// --------------

		Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		
		Composite textComp = new Composite(comp, 0);
		FillLayout fillLayout = new FillLayout(SWT.BORDER);
		textComp.setLayout(fillLayout);
		textComp.setBounds(100, 100, 0, 0);

		
		Link text = NLTranslatorTest.getHyperLinkedText(proc.getBody(), variablesRolesExplanation).create(textComp, SWT.BORDER); 
		
		System.out.println(text.toString());
		//text.setText(text.getText() + g.newline().words("asdasdadsdasd").create(textComp, SWT.BORDER));
		//text.
		// --------------

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	static void showDialog() {
		MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
		dialog.setText("My info");
		dialog.setMessage("Do you really want to do this?");

		dialog.open();
	}

}
