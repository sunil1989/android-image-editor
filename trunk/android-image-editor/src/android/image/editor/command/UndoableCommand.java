package android.image.editor.command;

public interface UndoableCommand extends Command {
	
	void undo();

}
