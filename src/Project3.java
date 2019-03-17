/**
* @author Dharshan Vishwanatha
* I have tested the program and 
* works as expected except for
* one bug.
* 
* The program has one issue where
* if the text area has a spacing
* at the end, it breaks the highlighting
* behavior. For example if the
* text area was ('_'<-spacing): 
* "Hello Hello_"
* "Hello Hello_".
* When finding the third Hello. 
* It would look
* like this: 
* '(' <- start of highlight, 
* ')'<- end of highlight.
* "Hello Hello_"
* "(Hell)o Hello_".
* If there is no space, it works fine.
* 
* Extra functionality: I added
* 3 additional components:
* 2 JButton and 1 JTextarea.
* Combined they serve the purpose
* of compiling a java code typed in the
* text editor and run the java file.
* The other JTextarea located
* at the bottom of the window, is
* to display any errors that the program
* encounters when compiling the code, and
* will display the errors there.
* For example, if one were to copy this
* entire code and paste it in the text
* editor and click compile and run. It
* would open up a new text editor from
* the text editor you used.
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

class WordPosition{
	int p1;
	int p2;
	public WordPosition(int p1,int p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
}
public class Project3 extends JPanel {
	JTextArea textEditor;
	JTextArea errorTextArea;
	JTextField findField;
	JTextField replaceField;
	
	JButton openButton;
	JButton saveButton;
	JButton findButton;
	JButton replaceButton;
	JButton compileButton;
	JButton runButton;
	
	JComboBox<Integer> fontSize;
	JComboBox<String> fontStyle;
	
	Dimension optionSize = new Dimension(100, 26);
	Integer[] fontSizeItems;
	ArrayList<WordPosition> finder;
	String findWord = "";
	String savedFilePath;
	Highlighter highlighter;
	
	boolean textAreaChanged = false;
	boolean compiled = false;
	int findIndex = 0;
	final HighlightPainter painter = 
            new DefaultHighlighter.
            DefaultHighlightPainter(Color.YELLOW);
	
	final String[] fontStyleItems = {"Plain","Bold","Italics"};
	final String desktopPath = System.getProperty("user.dir");
	final String javacPath = "C:\\Program Files\\Java"
			+ "\\jdk-10.0.2\\bin\\javac.exe";
	final String javaPath = "\"C:\\Program Files\\Java"
			+ "\\jdk-10.0.2\\bin\\java.exe\"";
	
	/**
	 * This function is a constructor
	 * that initializes and wires
	 * all the components together
	 */
	public Project3() {
		createComponent();
		wireComponent();
	}
	
	/**
	 * This function creates all
	 * the JComponents and initializes
	 * them to a default state.
	 */
	void createComponent() {
		//Initialize all components
		setLayout(new BorderLayout());
		textEditor = new JTextArea();
		errorTextArea = new JTextArea();
		openButton = new JButton("Open");
		saveButton = new JButton("Save");
		findButton = new JButton("Find");
		replaceButton = new JButton("Replace");
		compileButton = new JButton("Compile");
		runButton = new JButton("Run");
		findField = new JTextField();
		replaceField = new JTextField();
		fontSizeItems = new Integer[17];
		fontStyle = new JComboBox<>(fontStyleItems);
		finder = new ArrayList<>();
		
		//Assigns values to components
		textEditor.setTabSize(2);
		int index = 0;
		for(int i=8;i<=40;i+=2) {
			fontSizeItems[index] = i;
			index++;
		}
		fontSize = new JComboBox<>(fontSizeItems);
		fontSize.setSelectedIndex(2);
		errorTextArea.setText("Error outputs here...");
		errorTextArea.setEditable(false);
		errorTextArea.setFont(new Font(null, 0, 17));
		errorTextArea.setLineWrap(true);
		errorTextArea.setWrapStyleWord(true);
		
		openButton.setPreferredSize(optionSize);
		saveButton.setPreferredSize(optionSize);
		findButton.setPreferredSize(optionSize);
		replaceButton.setPreferredSize(optionSize);
		
		highlighter = textEditor.getHighlighter();
		
		//Add all components to panels
		JScrollPane scrollPane = new JScrollPane(textEditor);
		JPanel navigationPanel = new JPanel(new BorderLayout());
		JPanel optionsPanel = new JPanel(new GridLayout(2,5,5,5));
		JPanel executePanel = new JPanel(new GridLayout(2,1));
		JPanel errorPanel = new JPanel(new BorderLayout());
		
		optionsPanel.add(openButton);
		optionsPanel.add(fontSize);
		optionsPanel.add(findField);
		optionsPanel.add(findButton);
		
		optionsPanel.add(saveButton);
		optionsPanel.add(fontStyle);
		optionsPanel.add(replaceField);
		optionsPanel.add(replaceButton);
		
		executePanel.add(compileButton);
		executePanel.add(runButton);
		
		errorPanel.add(errorTextArea);
		
		navigationPanel.add(optionsPanel,BorderLayout.CENTER);
		navigationPanel.add(errorPanel, BorderLayout.SOUTH);
		
		//Add panels to the main frame
		this.add(scrollPane,BorderLayout.CENTER);
		this.add(navigationPanel, BorderLayout.SOUTH);
		this.add(executePanel, BorderLayout.EAST);
	}
	
	/**
	 * This function wires and
	 * adds functionality of the
	 * program. 
	 */
	void wireComponent() {
		
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		fontSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newFontSize = (Integer)fontSize.getSelectedItem();
				Font font = new Font(null, 
						textEditor.getFont().getStyle(),
						newFontSize);
				textEditor.setFont(font);
			}
		});
		
		fontStyle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = new Font(
						null,fontStyle.getSelectedIndex(), 
						textEditor.getFont().getSize());
				textEditor.setFont(font);
			}
		});

		findButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(findField.getText().equals("")) {
					return;
				}
				boolean doFind = (textAreaChanged ||
						!findWord.equals(findField.getText()));
				if(doFind) {
					finder.clear();
					textEditor.getHighlighter().removeAllHighlights();
					findWord = findField.getText();
					findAllWords();
					if(finder.size() == 0) {
						JOptionPane.showMessageDialog(new JFrame(), 
								findWord + " was not "
										+ "found in the document",
								"Text not found",
								JOptionPane.INFORMATION_MESSAGE);
						findIndex = 0;
						return;
					}
				}
				highlightNextWord();
			}
		});
	
		replaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tempText = textEditor.getText();
				tempText = tempText.replaceAll(findField.getText(),
						replaceField.getText());
				textEditor.setText(tempText);
			}
		});
	
		compileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "";
				errorTextArea.setForeground(Color.RED);
				compiled = false;
				if(textAreaChanged) {
					message = "SAVE FIRST";
				}
				else if(textEditor.getText().equals("")) {
					message = "NO CODE";
				}
				else {
					try {
						message = compile();
						if(message == "") {
							errorTextArea.setForeground(Color.GREEN);
							message = "COMPILED";
							compiled = true;
						}
					} catch (IOException e1) {
						errorTextArea.setText(e1.getMessage());
					}
				}
				errorTextArea.setText(message);
			}
		});

		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(compiled) {
					errorTextArea.setText("");
					try {
						run();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					errorTextArea.setForeground(Color.RED);
					errorTextArea.setText("COMPILE FIRST");
				}
			}
		});
		
		textEditor.getDocument().addDocumentListener(
				new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				
			}
		});
	}
	
	/**
	 * This function opens a
	 * file using the JFileChoose
	 * class and displays all
	 * the contents from that file
	 * into the text area.
	 */
	void openFile() {
		JFileChooser fileChooser = new JFileChooser(desktopPath);
		int returnVal = fileChooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	String filePath = fileChooser.getSelectedFile().getPath();
            File file = new File(filePath);
            textEditor.setText("");
            try {
            	Scanner in = new Scanner(file);
            	while(in.hasNextLine()) {
            		textEditor.append(in.nextLine()+'\n');
            	}
            	in.close();
            	textAreaChanged = false;
            	savedFilePath = filePath;
            } catch (Exception ex) {
            	errorTextArea.setForeground(Color.RED);
				errorTextArea.setText("Error loding file");
			}
        }
	}
	
	/**
	 * This function requests
	 * the JFileChooser to create
	 * a new file and save it 
	 * under that file, or override
	 * an existing file.
	 */
	void saveFile() {
		JFileChooser fileChooser = new JFileChooser(desktopPath);
		int returnVal = fileChooser.showSaveDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File filePath = fileChooser.getSelectedFile();
			try {
				PrintWriter writer = new PrintWriter(filePath);
				Scanner out = new Scanner(textEditor.getText());
				while(out.hasNextLine()) {
					writer.println(out.nextLine());
				}
				out.close();
				writer.close();
				textAreaChanged =false;
				savedFilePath = filePath.getPath();
				errorTextArea.setText("");
			} catch (Exception ex) {
				errorTextArea.setForeground(Color.RED);
				errorTextArea.setText("Error saving file");
			}
		}
	}
	
	/**
	 * This function uses
	 * the javac CMD command to
	 * compile the written java code
	 * in the text area. It uses
	 * the ProcessBuilder Class
	 * to execute the compile process.
	 * @return String - which consists
	 * of any error message while compiling
	 * the code.
	 * @throws IOException
	 */
	String compile() throws IOException {
		String line = "";
		String[] command = {
				javacPath,
				savedFilePath
		};
		ProcessBuilder pb=new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process=pb.start();
		BufferedReader inStreamReader = new BufferedReader(
				new InputStreamReader(process.getInputStream())); 

		line = inStreamReader.readLine();
		if(line == null) {
			line = "";
			compiled = true;
		}
		return line;
	}

	/**
	 * This function uses the java
	 * CMD command to run the compiled
	 * java file. It uses
	 * the ProcessBuilder Class
	 * to execute the run process.
	 * @throws IOException
	 */
	void run() throws IOException {
		int lastSlash = savedFilePath.lastIndexOf("\\");
		String pathDir = savedFilePath.substring(0,lastSlash);
		int dotPos = savedFilePath.lastIndexOf(".");
		String fileName = savedFilePath.substring(lastSlash+1,dotPos);
		String[] command = {
				"cmd.exe",
				"/c",
				"cd",
				pathDir,
				"&",
				"Start",
				"cmd.exe",
				"/k",
				javaPath,
				fileName
		};
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(command);
	}
	
	/**
	 * This function gets the name
	 * of the file from a given path.
	 * @param filePath - path of the file in disk.
	 * @return
	 */
	String getFileName(String filePath) {
		int lastSlash = savedFilePath.lastIndexOf("\\");
		int dotPos = savedFilePath.lastIndexOf(".");
		String fileName = savedFilePath.substring(lastSlash+1,dotPos);
		return fileName;
	}

	/**
	 * This function searches 
	 * through the entire
	 * text area of string and 
	 * finds all the words that
	 * match given word in the
	 * find field. It stores
	 * the start and end indexes
	 * of the words in a array list.
	 */
	void findAllWords() {
		Scanner in = new Scanner(textEditor.getText());
		int head = 0;
		while (in.hasNext()) {
			String value = in.next();
			if (value.equals(findWord)) {
				int tail = value.length();
				finder.add(new WordPosition(head, tail + head +1));
			}
			head += (value.length()+1);
		}
		in.close();
	}
	
	/**
	 * This function highlights
	 * the next word using the 
	 * indexes from the array list.
	 */
	void highlightNextWord() {
		if(findIndex < finder.size()) {
			textEditor.getHighlighter().removeAllHighlights();
			try {
				highlighter.addHighlight(finder.get(findIndex).p1, 
						finder.get(findIndex).p2-1, painter);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			findIndex++;
		}
		else {
			findIndex = 0;
		}
	}
	
	/**
	 * This function resets
	 * all the values that 
	 * need resetting when the
	 * user types in the
	 * text area.
	 */
	void textChanged() {
		textAreaChanged = true;
		finder.clear();
		findIndex = 0;
		textEditor.getHighlighter().removeAllHighlights();
		errorTextArea.setText("");
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Text Editor");
		frame.add(new Project3());
		frame.setPreferredSize(new Dimension(600, 450));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(frame, 
						"Are you sure you want to close this application?",
						"Confirm Exit", JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					frame.dispose();
				}
			}
		});
	}
}
