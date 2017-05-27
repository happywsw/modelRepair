package org.geneticrepair.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import org.processmining.converting.HNNetToEPCConverter;
import org.processmining.exporting.epcs.EpmlExport;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.log.LogSummary;
import org.processmining.framework.log.classic.LogReaderClassic;
import org.processmining.framework.log.filter.DefaultLogFilter;
import org.processmining.framework.log.rfb.fsio.FS2VirtualFileSystem;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.framework.ui.Message;
import org.processmining.framework.ui.menus.LogReaderMenu;
import org.processmining.framework.util.StopWatch;
import org.processmining.importing.epml.epmlImport;
import org.processmining.mining.MiningResult;
import org.processmining.mining.epcmining.EPCResult;

import gui.action.NewAction;
import nl.tue.declare.appl.design.constraint.gui.IConstraintGroupActionListener;
import nl.tue.tm.is.epc.EPC;

public class GeneticRepairUtil {
	DefaultLogFilter logFilter;
	LogReader logReader;
	private int numIndividuals = 10;
	private File individualDir = null;

	public GeneticRepairUtil() {

	}

	public LogReader readLog(String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		LogFile logFile = LogFile.getInstance(file.getAbsolutePath());
		LogReader logReader = LogReaderFactory.createInstance(new DefaultLogFilter(DefaultLogFilter.INCLUDE), logFile);
		return logReader;
	}

	public ConfigurableEPC readEPC(String filePath) throws IOException {
		InputStream input = new FileInputStream(filePath);
	//	EPC epc = EPC.loadEPML(filePath);
		epmlImport epmlImp = new epmlImport();
		EPCResult epc = (EPCResult) epmlImp.importFile(input);
		System.out.println("epc result:"+epc);
		//epc.cleanEPC();
		ConfigurableEPC conEPC = epc.getEPC();
		
		return conEPC;
	}

	public void EPCexport() {

	}

	public String createHead() {
		String log = "";
		int i = 0;
		for (i = 0; i < GeneticRepairConstants.logLine.length - 1; i++) {
			log += GeneticRepairConstants.logLine[i] + "||";
		}
		log += GeneticRepairConstants.logLine[i];
		return log;
	}

	public void write(int runNum, HeuristicsNet net, String path, long time) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
		// 文件长度，字节数
		long fileLength = randomFile.length();
		// 将写文件指针移到文件尾。
		randomFile.seek(fileLength);
		randomFile.writeBytes("runNum:" + runNum + " <|>  bestFitness:" + net.getFitness() + " <|> logFitness:"
				+ net.getLogFitness() + " <|> similarity:" + net.getSimilarity() + " <|> time:" + convertElapsedTime(TimeUnit.NANOSECONDS.toSeconds(time))+"\n");
		randomFile.close();

	}

	
	

	public void exportHN(String filePath, HeuristicsNet net) {
		try {
			OutputStream out = new FileOutputStream(new File(filePath));
			if (out == null) {
				System.out.println("file Path is error.....");
			}
			net.toFile(out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exportEPC(String path, HeuristicsNet net) {
		OutputStream out;
		try {
			out = new FileOutputStream(new File(path));
			EPCResult epcResult = new HNNetToEPCConverter().convert(net);
			ConfigurableEPC epc = epcResult.getEPC();
			EpmlExport export = new EpmlExport();
			ProvidedObject object = new ProvidedObject(epc.getName(), epc);
			export.export(object, out);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String convertElapsedTime(long elapsedTime) {

		int day = 24 * 60 * 60 * 1000; // in milliseconds
		int hour = 60 * 60 * 1000;
		int minute = 60 * 1000;
		int second = 1000;
		int time = Integer.parseInt("" + elapsedTime);
		int result = 0;
		int rest = 0;
		char separator = ':';
		StringBuffer sb = new StringBuffer();

		result = time / day;
		sb.append(result).append(separator);
		rest = time % day;
		result = rest / hour;
		sb.append(result).append(separator);
		rest = rest % hour;
		result = rest / minute;
		sb.append(result).append(separator);
		rest = rest % minute;
		result = rest / second;
		sb.append(result).append('.');
		rest = rest % second;
		sb.append(rest);

		return sb.toString();
	}

	
	
	
	public static void main(String[] args) {

	}
}
