package pt.iscte.paddle.codexplainer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import pt.iscte.paddle.javardise.ClassWidget;
import pt.iscte.paddle.javardise.Constants;
import pt.iscte.paddle.javardise.service.IClassWidget;
import pt.iscte.paddle.javardise.service.ICodeDecoration;
import pt.iscte.paddle.javardise.service.IDeclarationWidget;
import pt.iscte.paddle.javardise.service.IJavardiseService;
import pt.iscte.paddle.javardise.service.IWidget;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IBlock.IVisitor;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.temp.TestMaxArray;
import pt.iscte.paddle.codexplainer.temp.TestMaxMatrix;
import pt.iscte.paddle.codexplainer.temp.TestAbs;
import pt.iscte.paddle.codexplainer.temp.TestArrayFind;
import pt.iscte.paddle.codexplainer.temp.TestBoolean;
import pt.iscte.paddle.codexplainer.temp.TestFactorialRecursive;
import pt.iscte.paddle.codexplainer.temp.TestFactorialRecursive2;
import pt.iscte.paddle.codexplainer.temp.TestIsPrime;
//import pt.iscte.paddle.codexplainer.temp.TestMatrix;
import pt.iscte.paddle.codexplainer.temp.TestMatrixFind;
import pt.iscte.paddle.codexplainer.temp.TestNaturals;
import pt.iscte.paddle.codexplainer.temp.TestProcedure;
import pt.iscte.paddle.codexplainer.temp.TestProcedureCall;
import pt.iscte.paddle.codexplainer.temp.TestProcedureObjects;
import pt.iscte.paddle.codexplainer.temp.TestRecord;
import pt.iscte.paddle.codexplainer.temp.TestSelection;
import pt.iscte.paddle.codexplainer.temp.TestSubArray;
import pt.iscte.paddle.codexplainer.temp.TestSum;
import pt.iscte.paddle.codexplainer.temp.TestSumMatrix;
import pt.iscte.paddle.codexplainer.temp.TestSumMatrixLine;


public class ExplanationVisual {

	private static Shell shell;

	public static void main(String[] args) {
		TestMaxArray testMaxArray = new TestMaxArray();
		testMaxArray.setup();
		IModule modMaxArray = testMaxArray.getModule();
		IProcedure max = modMaxArray.getProcedure("max");
		
		TestSelection testSelection = new TestSelection();
		testSelection.setup();
		IModule modSelection = testSelection.getModule();
		IProcedure selection = modSelection.getProcedure("max");
			
		TestSum testSum = new TestSum();
		testSum.setup();
		IModule modSum = testSum.getModule();
		IProcedure sum = modSum.getProcedure("summation");
		
//		TestMaxMatrix testMaxMatrix = new TestMaxMatrix();
//		testMaxMatrix.setup();
//		IModule modMaxMatrix = testMaxMatrix.getModule();
//		IProcedure maxMatrix = modMaxMatrix.getProcedure("max");
		
		TestNaturals testNaturals = new TestNaturals();
		testNaturals.setup();
		IModule modNaturals = testNaturals.getModule();
		IProcedure naturals = modNaturals.getProcedure("naturals");
		
		TestIsPrime testisPrime = new TestIsPrime();
		testisPrime.setup();
		IModule modisPrime = testisPrime.getModule();
		IProcedure isPrime = modisPrime.getProcedure("isPrime");
		
		TestFactorialRecursive testFactorial = new TestFactorialRecursive();
		testFactorial.setup();
		IModule modFactorial = testFactorial.getModule();
		IProcedure factorial = modFactorial.getProcedure("factorial");
		
		TestFactorialRecursive2 testFactorial2 = new TestFactorialRecursive2();
		testFactorial2.setup();
		IModule modFactorial2 = testFactorial2.getModule();
		IProcedure factorial2 = modFactorial2.getProcedure("factorial");
		
		TestProcedureCall testProcedureCall = new TestProcedureCall();
		testProcedureCall.setup();
		IModule modProcedureCall = testProcedureCall.getModule();
		IProcedure procedureCall = modProcedureCall.getProcedure("factorial");
		
		TestArrayFind testArrayFind = new TestArrayFind();
		testArrayFind.setup();
		IModule modArrayFind = testArrayFind.getModule();
		IProcedure arrayFind = modArrayFind.getProcedure("exists");
		
		TestMatrixFind testMatrixFind = new TestMatrixFind();
		testMatrixFind.setup();
		IModule modMatrixFind = testMatrixFind.getModule();
		IProcedure matrixFind = modMatrixFind.getProcedure("contains");
		
		TestAbs testAbs = new TestAbs();
		testAbs.setup();
		IModule modAbs = testAbs.getModule();
		IProcedure abs = modAbs.getProcedure("abs");
		
		TestSubArray testSubArray = new TestSubArray();
		testSubArray.setup();
		IModule modSubArray = testSubArray.getModule();
		IProcedure subArray = modSubArray.getProcedure("subArray");
		
		TestProcedure testProcedure = new TestProcedure();
		testProcedure.setup();
		IModule modProcedure = testProcedure.getModule();
		IProcedure procedure = modProcedure.getProcedure("multiplyArrayValues");
		
		TestProcedureObjects testProcedureObj = new TestProcedureObjects();
		testProcedureObj.setup();
		IModule modProcedureObj = testProcedureObj.getModule();
		IProcedure procedureObj = modProcedureObj.getProcedure("enlarge");
		
		TestRecord testRecord = new TestRecord();
		testRecord.setup();
		IModule modRecord = testRecord.getModule();
		IProcedure record = modRecord.getProcedure("recordTest");
		
		TestBoolean testBoolean = new TestBoolean();
		testBoolean.setup();
		IModule modBoolean = testBoolean.getModule();
		IProcedure booleanCondition = modBoolean.getProcedure("testCondition");
		
//		TestMatrix testMatrix = new TestMatrix();
//		testMatrix.setup();
//		IModule modMatrixTest = testMatrix.getModule();
//		IProcedure matrix = modMatrixTest.getProcedure("matrixTest");
		
		TestSumMatrix testSumMatrix = new TestSumMatrix();
		testSumMatrix.setup();
		IModule modSumMatrix = testSumMatrix.getModule();
		IProcedure sumMatrix = modSumMatrix.getProcedure("sumMatrix");
		
		TestSumMatrixLine testSumMatrixLine = new TestSumMatrixLine();
		testSumMatrixLine.setup();
		IModule modSumMatrixLine = testSumMatrixLine.getModule();
		IProcedure sumMatrixLine = modSumMatrixLine.getProcedure("sumMatrixLine");
		
		
		//Recursive
		openWindow(factorial2, modFactorial2);
		openWindow(factorial, modFactorial);
//		
		openWindow(sum, modSum);
		openWindow(max, modMaxArray);
		openWindow(selection, modSelection);
		openWindow(procedure, modProcedure);
		openWindow(booleanCondition, modBoolean);
		openWindow(naturals, modNaturals);
		openWindow(isPrime, modisPrime);
		openWindow(procedureObj, modProcedureObj);
		openWindow(matrixFind, modMatrixFind);
		openWindow(arrayFind, modArrayFind);
		openWindow(abs, modAbs);
//		openWindow(matrix, modMatrixTest);
//		openWindow(sumMatrixLine, modSumMatrixLine);
		openWindow(subArray, modSubArray);
		
		
		openWindow(procedureCall, modProcedureCall);
		
		//Test Matrix
		
//		openWindow(sumMatrix, modSumMatrix);
//		openWindow(maxMatrix, modMaxMatrix);		
//		//Test method classification
		
//		openWindow(record, modRecord);
	
	}


		
	public static void openWindow(IProcedure proc, IModule module) {

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

		IClassWidget widget = IJavardiseService.createClassWidget(comp, module);
		widget.setReadOnly(true);

		//System.out.println(proc.getBody().getChildren());
		//List<IBlockElement> children = proc.getBody().getChildren();

		Composite textComp = new Composite(comp, 0);
		FillLayout fillLayout = new FillLayout(SWT.BORDER);
		textComp.setLayout(fillLayout);
		textComp.setBounds(100, 100, 0, 0);

		Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		
		
		//Explanation 
		ExplanationGenerator gen = new ExplanationGenerator(proc);
		
		//HyperlinkedText hypertext2 = new HyperlinkedText(e -> e.forEach(e2 -> widget.addMark(blue).show()));
		
		HyperlinkedText hypertext = new HyperlinkedText(e -> e.forEach(e2 -> IJavardiseService.getWidget(e2).addMark(blue).show()));

		convertExplanationtoLinkText(gen.getExplanation(), hypertext);
		Link text = hypertext.create(textComp, SWT.BORDER);
		//System.out.println(text.getText());

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

	static void convertExplanationtoLinkText(List<List<TextComponent>> explanationText, HyperlinkedText hypertext) {
		for (List<TextComponent> line : explanationText) {
			for (TextComponent comp : line) {
		
				if (comp.getType().equals(TextType.NORMAL)) {
					hypertext.words(comp.getText());
					System.out.print(comp.getText());
				}
				if (comp.getType().equals(TextType.LINK)) {
					hypertext.link(comp.getText(), comp.getElement());
					System.out.print(comp.getText());
				}
				if (comp.getType().equals(TextType.NEWLINE)) {
					hypertext.newline();
					System.out.print(comp.getText());
				}
			}
			System.out.print("\n");
			hypertext.newline();
		}
	}
	
	private static void createOtherGroup(IProcedure proc, Display display) {
		Group groupOther = new Group(shell, SWT.BORDER);
		groupOther.setText("other");
		groupOther.setLayout(new FillLayout());
		
		Image arrow = new Image(display, "arrow.png");
		List<ICodeDecoration<Label>> decAssignments = new ArrayList<>();

		Button markAssignment = new Button(groupOther, SWT.PUSH);
		markAssignment.setText("mark assignments");
		markAssignment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(decAssignments.isEmpty()) {
					proc.accept(new IVisitor() {
						public boolean visit(IVariableAssignment a) {
							IWidget w = IJavardiseService.getWidget(a);
							ICodeDecoration<Label> d = w.addImage(arrow, ICodeDecoration.Location.LEFT);
							decAssignments.add(d);
							return true;
						}
					});
				}
				decAssignments.forEach(d -> d.show());
			}
		});


		Button removeMarkAssignment = new Button(groupOther, SWT.PUSH);
		removeMarkAssignment.setText("remove mark assignments");
		removeMarkAssignment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				decAssignments.forEach(d -> d.hide());
			}
		});


		// --------------

		List<ICodeDecoration<Text>> returnMarks = new ArrayList<>();

		Button markReturn = new Button(groupOther, SWT.PUSH);
		markReturn.setText("mark return");
		markReturn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(returnMarks.isEmpty()) {
					proc.accept(new IVisitor() {
						public boolean visit(IReturn r) {
							IWidget w = IJavardiseService.getWidget(r);
							ICodeDecoration<Text> d = w.addNote("this is the end", ICodeDecoration.Location.RIGHT);
							returnMarks.add(d);
							return true;
						}
					});
				}
				returnMarks.forEach(d -> d.show());
			}
		});

		Button removeReturnMarks = new Button(groupOther, SWT.PUSH);
		removeReturnMarks.setText("delete return marks");
		removeReturnMarks.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				returnMarks.forEach(d -> d.delete());
				returnMarks.clear();
			}
		});
	}

	private static void createMarksGroup(IProcedure proc, Display display, IClassWidget widget) {
		Group groupMarks = new Group(shell, SWT.BORDER);
		groupMarks.setText("marks");
		groupMarks.setLayout(new FillLayout());
		groupMarks.setFocus();

		Button statementsButton = new Button(groupMarks, SWT.PUSH);
		statementsButton.setText("statements next");
		statementsButton.addSelectionListener(new SelectionAdapter() {
			Color red = new Color (display, 255, 0, 0);
			Iterator<IBlockElement> it = proc.getBody().deepIterator();
			IBlockElement element = it.hasNext() ? it.next() : null;
			ICodeDecoration<Canvas> mark;
			public void widgetSelected(SelectionEvent e) {
				if(mark != null)
					mark.delete();

				if(element != null) {
					IWidget w = IJavardiseService.getWidget(element);
					mark = w.addMark(red);
					mark.show();
				}
				element = it.hasNext() ? it.next() : null;	
			}
		});

		Button expressionsButton = new Button(groupMarks, SWT.PUSH);
		expressionsButton.setText("expressions");
		expressionsButton.addSelectionListener(new SelectionAdapter() {
			Color blue = new Color (display, 0, 0, 255);
			Color green = new Color (display, 0, 255, 0);
			public void widgetSelected(SelectionEvent e) {
				proc.accept(new IBlock.IVisitor() {
					public void visitAny(IExpression exp) {
						IWidget w = IJavardiseService.getWidget(exp);
						if(w != null)
							w.addMark(exp.isSimple() ? green : blue).show();
						else
							System.err.println(exp);
					}
				});
			}
		});
		
		Button regionMark = new Button(groupMarks, SWT.PUSH);
		regionMark.setText("region mark");
		regionMark.addSelectionListener(new SelectionAdapter() {
			Color cyan = Display.getDefault().getSystemColor(SWT.COLOR_CYAN);
			public void widgetSelected(SelectionEvent e) {
				IDeclarationWidget a = IJavardiseService.getDeclarationWidget(proc.getVariables().get(1));
				IWidget w1 = IJavardiseService.getWidget(proc.getBody().getChildren().get(1));
				IWidget w2 = IJavardiseService.getWidget(proc.getBody().getChildren().get(2));
				a.addRegionMark(cyan, w1, w2).show();
			}
		});

		Button otherMarks = new Button(groupMarks, SWT.PUSH);
		otherMarks.setText("detail marks");
		otherMarks.addSelectionListener(new SelectionAdapter() {
			Color cyan = Display.getDefault().getSystemColor(SWT.COLOR_CYAN);
			Color magenta = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
			public void widgetSelected(SelectionEvent e) {
				ICodeDecoration<Canvas> dec = widget.getClassName().addMark(cyan);
				dec.show();
				widget.getProcedure(proc).getReturnType().addMark(cyan).show();
				widget.getProcedure(proc).getMethodName().addMark(magenta).show();
				IDeclarationWidget decDec = IJavardiseService.getDeclarationWidget(proc.getParameters().get(0));
				decDec.getVariableType().addMark(cyan).show();
				decDec.getVariableName().addMark(magenta).show();
			}
		});
	}

}
