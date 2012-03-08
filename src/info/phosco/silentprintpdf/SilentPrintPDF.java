package info.phosco.silentprintpdf;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterName;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageable;

public class SilentPrintPDF {

	private PrintService getPrintServiceForName(String printer) {
		PrintService res = null;
		PrintService[] services = PrinterJob.lookupPrintServices();

		for (PrintService ps : services) {
			PrinterName name = ps.getAttribute(PrinterName.class);
			if (name.getValue().equalsIgnoreCase(printer)) {
				res = ps;
				break;
			}
		}
		return res;
	}

	private void silentPrint(DocPrintJob job, PDDocument pdf)
		throws PrinterException {

		if (job == null) {
			throw new PrinterException("The given printer job is null.");
		}

		try {
			Doc doc = new SimpleDoc(new PDPageable(pdf), DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
			job.print(doc, new HashPrintRequestAttributeSet());
		} catch (final PrintException ex) {
			PrinterException pe = new PrinterException();
			pe.initCause(ex);
			throw pe;
		}

	}

	public void print(String[] args) {
		String file = null;
		String printer = null;

		file = args[0];
		if (args.length == 2) {
			printer = args[1];
		}
		
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);

			PrintService ps = getPrintServiceForName(printer);
			if (ps != null) {
				silentPrint(ps.createPrintJob(), doc);
			} else {
				doc.silentPrint();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException ex1) {
					ex1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

		if ((args.length < 1) || (args.length > 2)) {
			System.out.println("Usage: silentprintpdf <PDF file> [<printer name>]");
			System.exit(1);
		}

		SilentPrintPDF service = new SilentPrintPDF();
		service.print(args);
	}

}
