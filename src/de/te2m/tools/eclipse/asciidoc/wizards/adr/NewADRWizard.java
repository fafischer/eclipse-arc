package de.te2m.tools.eclipse.asciidoc.wizards.adr;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

/**
 * This is a wizard for creating a new ADR (Architecture Decision Record).
 */

public class NewADRWizard extends Wizard implements INewWizard {
	
	/** The page. */
	private NewADRWizardPage page;
	
	/** The selection. */
	private ISelection selection;

	/**
	 * Constructor for NewADRWizard.
	 */
	public NewADRWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new NewADRWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		IRunnableWithProgress op = monitor -> {
			try {
				doFinish(containerName, fileName, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 *
	 * @param containerName the container name
	 * @param fileName the file name
	 * @param monitor the monitor
	 * @throws CoreException the core exception
	 */

	private void doFinish(
		String containerName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
			}
		});
		monitor.worked(1);
	}

	/**
	 * Open content stream.
	 *
	 * @return the input stream
	 */
	private InputStream openContentStream() {
		// TODO Externalize initial content
		String contents =
			"=== ADR xxx Template\n" + 
			"\n" + 
			"==== Title\n" + 
			"\n" + 
			"short present tense imperative phrase, less than 50 characters, like a git commit message.\n" + 
			"\n" + 
			"==== Status\n" + 
			"\n" + 
			"proposed, accepted, rejected, deprecated, superseded, etc.\n" + 
			"\n" + 
			"==== Context\n" + 
			"\n" + 
			"what is the issue that we're seeing that is motivating this decision or change.\n" + 
			"\n" + 
			"==== Decision\n" + 
			"\n" + 
			"what is the change that we're actually proposing or doing.\n" + 
			"\n" + 
			"==== Consequences\n" + 
			"\n" + 
			"what becomes easier or more difficult to do because of this change.";
		return new ByteArrayInputStream(contents.getBytes());
	}

	/**
	 * Throw core exception.
	 *
	 * @param message the message
	 * @throws CoreException the core exception
	 */
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "de.te2m.tools.eclipse.asciidoc", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 *
	 * @param workbench the workbench
	 * @param selection the selection
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}