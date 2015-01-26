package wmh;

import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import jxl.write.*;
import jxl.write.Number;
import jxl.Workbook;
import jxl.WorkbookSettings;

//wyniki
public class ResultWriter 
{
	Vector<RunResults> results = new Vector<>();
	
	public void writeResults()
	{
		try
		{
			WritableWorkbook skoroszyt = Workbook.createWorkbook(new File(Configuration.resultsPath+"\\wyniki.xls"));
			WritableSheet arkusz = skoroszyt.createSheet("Arkusz", 0);
			for(int i=0;i<11;++i)
				arkusz.setColumnView(i, i==0?40:25);
			WorkbookSettings ustawienia = new WorkbookSettings();
			ustawienia.setLocale(new Locale("pl", "PL"));
			WritableFont font = new WritableFont(WritableFont.COURIER, 10);
			WritableCellFormat tahoma = new WritableCellFormat(font);
			
			arkusz.mergeCells(0, 0, 0, 1);//name
			arkusz.mergeCells(1, 0, 1, 1);//n
			arkusz.mergeCells(2, 0, 2, 1);//q
			arkusz.mergeCells(3, 0, 3, 1);//gestosc
			arkusz.mergeCells(4, 0, 4, 1);//best cover
			arkusz.mergeCells(5, 0, 6, 0);//algorytm1
			arkusz.mergeCells(7, 0, 8, 0);//algorytm2
			arkusz.mergeCells(9, 0, 10, 0);//algorytm3
			
			Label tekst = new Label(0, 0, "Nazwa grafu", tahoma);
			arkusz.addCell(tekst);
			Label tekst2 = new Label(1, 0, "Liczba wierzcho³ków", tahoma);
			arkusz.addCell(tekst2);
			Label tekst3 = new Label(2, 0, "Liczba krawêdzi", tahoma);
			arkusz.addCell(tekst3);
			Label tekst4 = new Label(3, 0, "Gêstoœæ", tahoma);
			arkusz.addCell(tekst4);
			Label tekst5 = new Label(4, 0, "Najlepsze pokrycie", tahoma);
			arkusz.addCell(tekst5);
			Label tekst6 = new Label(5, 0, "DegreeGreedy", tahoma);
			arkusz.addCell(tekst6);
			Label tekst61 = new Label(5, 1, "Pokrycie", tahoma);
			arkusz.addCell(tekst61);
			Label tekst62 = new Label(6, 1, "Czas dzialania", tahoma);
			arkusz.addCell(tekst62);
			Label tekst7 = new Label(7, 0, "DFS", tahoma);
			arkusz.addCell(tekst7);
			Label tekst71 = new Label(7, 1, "Pokrycie", tahoma);
			arkusz.addCell(tekst71);
			Label tekst72 = new Label(8, 1, "Czas dzialania", tahoma);
			arkusz.addCell(tekst72);
			Label tekst8 = new Label(9, 0, "EdgeGreedy", tahoma);
			arkusz.addCell(tekst8);
			Label tekst81 = new Label(9, 1, "Pokrycie", tahoma);
			arkusz.addCell(tekst81);
			Label tekst82 = new Label(10, 1, "Czas dzialania", tahoma);
			arkusz.addCell(tekst82);
			
			WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);
			WritableCellFormat intFormat = new WritableCellFormat(NumberFormats.INTEGER);
			
			int row = 2;
			
			for(RunResults res: results)
			{
				PrintWriter writer;
				try 
				{
					String[] names = res.filename.split("\\\\");
					writer = new PrintWriter(Configuration.resultsPath+"\\"+names[names.length-1], "UTF-8");
//				
//					writer.println("========================");
//					writer.println("n:\t\t" + res.n);
//					writer.println("q:\t\t" + res.q);
//					writer.println("rozwiazanie optymalne:\t\t" + res.bestCover);
//					writer.println("========================");
//
//					Label name = new Label(0, row, names[names.length-1], tahoma);
//					arkusz.addCell(name);
//					Number n = new Number(1, row, res.n, intFormat);
//					arkusz.addCell(n);
//					Number q = new Number(2, row, res.q, intFormat);
//					arkusz.addCell(q);
//					Number density = new Number(3, row, (double)res.q/(double)res.n/(double)(res.n-1)*2.0, floatFormat);
//					arkusz.addCell(density);
//					Number coverSize = new Number(4, row, res.bestCover, intFormat);
//					arkusz.addCell(coverSize);
//					
//						writer.println("DegreeGreedy:");
//						writer.println("pokrycie:\t\t" + res.results[Method.AntAlgorithm].cover.size());
//						Integer[] cover = res.results[Method.AntAlgorithm].cover.toArray(new Integer[res.results[Method.AntAlgorithm].cover.size()]);
//						java.util.Arrays.sort(cover);
//						for(int i:cover)
//						{
//							writer.print(i + " ");
//						}
//						writer.println();
//						writer.println("czas:\t\t" + res.results[Method.AntAlgorithm].executionTime + " ns");
//						writer.println("========================");
//						
//
//						Number size = new Number(5, row, res.results[Method.AntAlgorithm].cover.size(), intFormat);
//						arkusz.addCell(size);
//						Number time = new Number(6, row, res.results[Method.AntAlgorithm].executionTime, intFormat);
//						arkusz.addCell(time);
//					
//					writer.close();
				} 
				catch (FileNotFoundException | UnsupportedEncodingException e) 
				{
					e.printStackTrace();
				}
				++row;
			}
			skoroszyt.write();
			skoroszyt.close();
		}
		catch( IOException e)
		{
			
		}
		catch( WriteException e)
		{
			
		}
		finally
		{
			
		}
	}
}
