package wmh;

import java.util.LinkedList;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.write.*;
import jxl.write.Number;
import jxl.Workbook;
import jxl.WorkbookSettings;

//wyniki
public class ResultWriter 
{
	@SuppressWarnings("deprecation")
	public void writeResults(LinkedList<RunResults> results)
	{
		try
		{
			WritableWorkbook skoroszyt = Workbook.createWorkbook(new File(Configuration.resultsPath));
			
			WritableSheet arkusz = skoroszyt.createSheet("Arkusz", 0);
			
			for(int i=0;i<11;++i)
				arkusz.setColumnView(i, i==0?40:25);
			WorkbookSettings ustawienia = new WorkbookSettings();
			ustawienia.setLocale(new Locale("pl", "PL"));
			WritableFont font = new WritableFont(WritableFont.COURIER, 10);
			WritableCellFormat textFormat = new WritableCellFormat(font);
			WritableCellFormat titleCell = new WritableCellFormat(font);
			titleCell.setBackground(Colour.AQUA);
			
			int col_idx = 0,
					col_n = 1,
					col_q = 2,
					col_density = 3,
					col_bestPossibleCost = 4,
					col_alpha = 5,
					col_beta = 6,
					col_fading = 7,
					col_Q = 8,
					col_maxInitPheromone = 9,
					col_numAnts = 10,
					col_bestCost = 11,
					col_meanCost = 12,
					col_meanTime = 13,
					col_meanEpochs = 14;
			
			Label tekst1 = new Label(col_idx, 0, "Numer przebiegu", titleCell);
			arkusz.addCell(tekst1);
			Label tekst2 = new Label(col_n, 0, "Liczba wierzcho³ków", titleCell);
			arkusz.addCell(tekst2);
			Label tekst3 = new Label(col_q, 0, "Liczba krawêdzi", titleCell);
			arkusz.addCell(tekst3);
			Label tekst4 = new Label(col_density, 0, "Gêstoœæ", titleCell);
			arkusz.addCell(tekst4);
			Label tekst5 = new Label(col_bestPossibleCost, 0, "Najlepszy mo¿liwy koszt", titleCell);
			arkusz.addCell(tekst5);
			Label tekst6 = new Label(col_alpha, 0, "alpha", titleCell);
			arkusz.addCell(tekst6);
			Label tekst7 = new Label(col_beta, 0, "beta", titleCell);
			arkusz.addCell(tekst7);
			Label tekst8 = new Label(col_fading, 0, "rho", titleCell);
			arkusz.addCell(tekst8);
			Label tekst9 = new Label(col_Q, 0, "Q", titleCell);
			arkusz.addCell(tekst9);
			Label tekst10 = new Label(col_maxInitPheromone, 0, "max pocz¹tkowy feromon", titleCell);
			arkusz.addCell(tekst10);
			Label tekst11 = new Label(col_numAnts, 0, "iloœæ mrówek", titleCell);
			arkusz.addCell(tekst11);
			Label tekst12 = new Label(col_bestCost, 0, "najlepszy koszt", titleCell);
			arkusz.addCell(tekst12);
			Label tekst13 = new Label(col_meanCost, 0, "œredni koszt", titleCell);
			arkusz.addCell(tekst13);
			Label tekst14 = new Label(col_meanTime, 0, "œredni czas", titleCell);
			arkusz.addCell(tekst14);
			Label tekst15 = new Label(col_meanEpochs, 0, "œrednia iloœæ cykli", titleCell);
			arkusz.addCell(tekst15);
			
			WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);
			WritableCellFormat intFormat = new WritableCellFormat(NumberFormats.INTEGER);
			
			int row = 2;
			
			for(RunResults res: results)
			{
				Label name = new Label(col_idx, row, String.valueOf(row-1), textFormat);
				arkusz.addCell(name);
				Number n = new Number(col_n, row, res.n, intFormat);
				arkusz.addCell(n);
				Number q = new Number(col_q, row, res.q, intFormat);
				arkusz.addCell(q);
				Number density = new Number(col_density, row, (double)res.q/(double)res.n/(double)(res.n-1)*2.0, floatFormat);
				arkusz.addCell(density);
				Number bestPossibleCost = new Number(col_bestPossibleCost, row, res.bestPossibleCost, floatFormat);
				arkusz.addCell(bestPossibleCost);
				Number alpha = new Number(col_alpha,row, res.weightAttractiveness, floatFormat);
				arkusz.addCell(alpha);
				Number beta = new Number(col_beta, row,res.pheromoneAttractiveness, floatFormat);
				arkusz.addCell(beta);
				Number rho = new Number(col_fading, row, res.pheromoneFadingRate, floatFormat);
				arkusz.addCell(rho);
				Number Q = new Number(col_Q, row, res.pheromoneQConstant, floatFormat);
				arkusz.addCell(Q);
				Number maxInitPheromone = new Number(col_maxInitPheromone, row, res.maxInitialPheromone, floatFormat);
				arkusz.addCell(maxInitPheromone);
				Number numAnts = new Number(col_numAnts, row, res.numberOfAnts, intFormat);
				arkusz.addCell(numAnts);
				Number bestCost = new Number(col_bestCost, row, res.bestFoundCost, floatFormat);
				arkusz.addCell(bestCost);
				Number meanCost = new Number(col_meanCost, row, res.meanFoundCost, floatFormat);
				arkusz.addCell(meanCost);
				Number meanTime = new Number(col_meanTime, row, res.meanExecutionTimeInNs/1000000, floatFormat);
				arkusz.addCell(meanTime);
				Number meanEpochs = new Number(col_meanEpochs, row, res.meanNumEpochs, floatFormat);
				arkusz.addCell(meanEpochs);

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
	
	@SuppressWarnings("deprecation")
	public void writePathsInEpochs(LinkedList<RunResults> results)
	{
		try
		{
			WritableWorkbook skoroszyt = Workbook.createWorkbook(new File(Configuration.resultsPath+"_paths.xls"));
			
			WritableSheet arkusz = skoroszyt.createSheet("Arkusz", 0);
			
			for(int i=0;i<11;++i)
				arkusz.setColumnView(i, i==0?40:25);
			WorkbookSettings ustawienia = new WorkbookSettings();
			ustawienia.setLocale(new Locale("pl", "PL"));
			WritableFont font = new WritableFont(WritableFont.COURIER, 10);
			WritableCellFormat textFormat = new WritableCellFormat(font);
			WritableCellFormat titleCell = new WritableCellFormat(font);
			titleCell.setBackground(Colour.AQUA);
			
			int row_idx = 0,
					row_n = 1,
					row_q = 2,
					row_density = 3,
					row_bestPossibleCost = 4,
					row_alpha = 5,
					row_beta = 6,
					row_fading = 7,
					row_Q = 8,
					row_maxInitPheromone = 9,
					row_numAnts = 10,
					row_bestCost = 11,
					row_meanCost = 12,
					row_meanTime = 13,
					row_meanEpochs = 14,
					row_meanPathsPerEpoch = 15;

			Label tekst1 = new Label(0, row_idx, "Numer przebiegu", titleCell);
			arkusz.addCell(tekst1);
			Label tekst2 = new Label(0, row_n, "Liczba wierzcho³ków", titleCell);
			arkusz.addCell(tekst2);
			Label tekst3 = new Label(0, row_q, "Liczba krawêdzi", titleCell);
			arkusz.addCell(tekst3);
			Label tekst4 = new Label(0, row_density, "Gêstoœæ", titleCell);
			arkusz.addCell(tekst4);
			Label tekst5 = new Label(0, row_bestPossibleCost, "Najlepszy mo¿liwy koszt", titleCell);
			arkusz.addCell(tekst5);
			Label tekst6 = new Label(0, row_alpha, "alpha (feromon)", titleCell);
			arkusz.addCell(tekst6);
			Label tekst7 = new Label(0, row_beta, "beta (krawedz)", titleCell);
			arkusz.addCell(tekst7);
			Label tekst8 = new Label(0, row_fading, "rho", titleCell);
			arkusz.addCell(tekst8);
			Label tekst9 = new Label(0, row_Q, "Q", titleCell);
			arkusz.addCell(tekst9);
			Label tekst10 = new Label(0, row_maxInitPheromone, "max pocz¹tkowy feromon", titleCell);
			arkusz.addCell(tekst10);
			Label tekst11 = new Label(0, row_numAnts, "iloœæ mrówek", titleCell);
			arkusz.addCell(tekst11);
			Label tekst12 = new Label(0, row_bestCost, "najlepszy koszt", titleCell);
			arkusz.addCell(tekst12);
			Label tekst13 = new Label(0, row_meanCost, "œredni koszt", titleCell);
			arkusz.addCell(tekst13);
			Label tekst14 = new Label(0, row_meanTime, "œredni czas", titleCell);
			arkusz.addCell(tekst14);
			Label tekst15 = new Label(0, row_meanEpochs, "œrednia iloœæ cykli", titleCell);
			arkusz.addCell(tekst15);
			Label tekst16 = new Label(0, row_meanPathsPerEpoch, "zbie¿noœæ w cyklach", titleCell);
			arkusz.addCell(tekst16);
			
			WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);
			WritableCellFormat intFormat = new WritableCellFormat(NumberFormats.INTEGER);
			
			int col = 2;
			
			for(RunResults res: results)
			{
				Label name = new Label(col, row_idx, String.valueOf(col-1), textFormat);
				arkusz.addCell(name);
				Number n = new Number(col, row_n, res.n, intFormat);
				arkusz.addCell(n);
				Number q = new Number(col, row_q, res.q, intFormat);
				arkusz.addCell(q);
				Number density = new Number(col,row_density, (double)res.q/(double)res.n/(double)(res.n-1)*2.0, floatFormat);
				arkusz.addCell(density);
				Number bestPossibleCost = new Number(col,row_bestPossibleCost,  res.bestPossibleCost, floatFormat);
				arkusz.addCell(bestPossibleCost);
				Number alpha = new Number(col,row_alpha, res.pheromoneAttractiveness, floatFormat);
				arkusz.addCell(alpha);
				Number beta = new Number(col,row_beta, res.weightAttractiveness, floatFormat);
				arkusz.addCell(beta);
				Number rho = new Number(col,row_fading,  res.pheromoneFadingRate, floatFormat);
				arkusz.addCell(rho);
				Number Q = new Number(col,row_Q,  res.pheromoneQConstant, floatFormat);
				arkusz.addCell(Q);
				Number maxInitPheromone = new Number(col,row_maxInitPheromone,  res.maxInitialPheromone, floatFormat);
				arkusz.addCell(maxInitPheromone);
				Number numAnts = new Number(col,row_numAnts,  res.numberOfAnts, intFormat);
				arkusz.addCell(numAnts);
				Number bestCost = new Number(col,row_bestCost,  res.bestFoundCost, floatFormat);
				arkusz.addCell(bestCost);
				Number meanCost = new Number(col,row_meanCost,  res.meanFoundCost, floatFormat);
				arkusz.addCell(meanCost);
				Number meanTime = new Number(col,row_meanTime,  res.meanExecutionTimeInNs/1000000, floatFormat);
				arkusz.addCell(meanTime);
				Number meanEpochs = new Number(col,row_meanEpochs,  res.meanNumEpochs, floatFormat);
				arkusz.addCell(meanEpochs);

				int startingIdx = row_meanPathsPerEpoch+1;
				for(double d: res.meanPathsInEpochs)
				{
					Number paths = new Number(col, startingIdx, d, floatFormat);
					arkusz.addCell(paths);
					++startingIdx;
				}

				++col;
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
	
	@SuppressWarnings("deprecation")
	public void writeAlfaBeta2D(LinkedList<RunResults> results, Vector<Double> beta, Vector<Double>  alfa)
	{
		try
		{
			WritableWorkbook skoroszyt = Workbook.createWorkbook(new File(Configuration.resultsPath+"_2d.xls"));
			
			WritableSheet arkusz1 = skoroszyt.createSheet("Koszt", 0);
			WritableSheet arkusz2 = skoroszyt.createSheet("Czas", 1);
			WritableSheet arkusz3 = skoroszyt.createSheet("Cykle", 2);
			
			WorkbookSettings ustawienia = new WorkbookSettings();
			ustawienia.setLocale(new Locale("pl", "PL"));
			WritableFont font = new WritableFont(WritableFont.COURIER, 10);
			WritableCellFormat titleCell = new WritableCellFormat(font);
			titleCell.setBackground(Colour.AQUA);

			WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);

			arkusz1.addCell(new Label(1, 0, "beta (krawedz)", titleCell));
			arkusz2.addCell(new Label(1, 0, "beta (krawedz)", titleCell));
			arkusz3.addCell(new Label(1, 0, "beta (krawedz)", titleCell));
			arkusz1.addCell(new Label(0, 1, "alfa (feromon)", titleCell));
			arkusz2.addCell(new Label(0, 1, "alfa (feromon)", titleCell));
			arkusz3.addCell(new Label(0, 1, "alfa (feromon)", titleCell));
			int startingIdx = 2;
			for(double d: alfa)
			{
				arkusz1.addCell(new Number(1, startingIdx, d, floatFormat));
				arkusz2.addCell(new Number(1, startingIdx, d, floatFormat));
				arkusz3.addCell(new Number(1, startingIdx, d, floatFormat));
				++startingIdx;
			}
			startingIdx = 2;
			for(double d: beta)
			{
				arkusz1.addCell(new Number(startingIdx, 1, d, floatFormat));
				arkusz2.addCell(new Number(startingIdx, 1, d, floatFormat));
				arkusz3.addCell(new Number(startingIdx, 1, d, floatFormat));
				++startingIdx;
			}
			
			for(RunResults res: results)
			{
				int col = beta.indexOf( res.weightAttractiveness);
				int row = alfa.indexOf( res.pheromoneAttractiveness);
				
				Number cost = new Number(2+col, 2+row, res.meanFoundCost, floatFormat);
				arkusz1.addCell(cost);
				Number meanExecutionTimeInNs = new Number(2+col, 2+row, res.meanExecutionTimeInNs/1000000, floatFormat);
				arkusz2.addCell(meanExecutionTimeInNs);
				Number meanNumEpochs = new Number(2+col, 2+row, res.meanNumEpochs, floatFormat);
				arkusz3.addCell(meanNumEpochs);
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
