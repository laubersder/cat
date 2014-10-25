package com.dianping.cat.consumer.performance;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.Constants;
import com.dianping.cat.ServerConfigManager;
import com.dianping.cat.consumer.MockReportManager;
import com.dianping.cat.consumer.cross.CrossAnalyzer;
import com.dianping.cat.consumer.cross.IpConvertManager;
import com.dianping.cat.consumer.cross.model.entity.CrossReport;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.internal.MockMessageBuilder;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import com.dianping.cat.service.HostinfoService;

public class CrossPerformanceTest extends ComponentTestCase {

	@Test
	public void test() throws Exception {
		CrossAnalyzer analyzer = new CrossAnalyzer();

		analyzer.setIpConvertManager(new IpConvertManager());
		analyzer.setServerConfigManager(new ServerConfigManager());
		analyzer.setReportManager(new MockCrossReportManager());
		analyzer.setHostinfoService(new MockHostinfoService());

		MessageTree tree = buildMessage();

		long current = System.currentTimeMillis();

		int size = 10000000;
		for (int i = 0; i < size; i++) {
			analyzer.process(tree);
		}
		System.out.println(analyzer.getReport("cat"));
		System.out.println(analyzer.getReport("server"));
		System.out.println("Cost " + (System.currentTimeMillis() - current) / 1000);
		// cost 26
	}

	public static class MockCrossReportManager extends MockReportManager<CrossReport> {

		private Map<Long, Map<String, CrossReport>> m_reports = new ConcurrentHashMap<Long, Map<String, CrossReport>>();;

		@Override
		public CrossReport getHourlyReport(long startTime, String domain, boolean createIfNotExist) {
			Map<String, CrossReport> reports = m_reports.get(startTime);

			if (reports == null && createIfNotExist) {
				reports = new ConcurrentHashMap<String, CrossReport>();
				m_reports.put(startTime, reports);
			}

			CrossReport report = reports.get(domain);

			if (report == null && createIfNotExist) {
				report = new CrossReport(domain);

				report.setStartTime(new Date(startTime));
				report.setEndTime(new Date(startTime + Constants.HOUR - 1));
				reports.put(domain, report);
			}
			return report;
		}
	}

	public static class MockHostinfoService extends HostinfoService {
		@Override
		public void initialize() throws InitializationException {
		}

		@Override
		public String queryDomainByIp(String ip) {
			return "Cat-CatTest";
		}
	}

	public MessageTree buildMessage() {
		Message message = new MockMessageBuilder() {
			@Override
			public MessageHolder define() {
				TransactionHolder t = t("URL", "GET", 112819)
				      .child(
				            t("PigeonCall",
				                  "groupService:groupNoteService_1.0.0:updateNoteDraft(Integer,Integer,String,String)", "",
				                  100).child(e("PigeonCall.server", "10.1.2.99:2011", "Execute[34796272]")).child(
				                  e("PigeonCall.app", "server", "")))
				      .child(
				            t("PigeonCall",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft1(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonCall.server", "10.1.2.199:2011", "Execute[34796272]")).child(
				                  e("PigeonCall.app", "server", "")))
				      .child(
				            t("PigeonCall",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft2(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonCall.server", "10.1.2.199:2011", "Execute[34796272]")).child(
				                  e("PigeonCall.app", "server", "")))
				      .child(
				            t("PigeonCall",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft3(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonCall.server", "lion.dianpingoa.com:2011", "Execute[34796272]"))
				                  .child(e("PigeonCall.app", "server", "")))
				      .child(
				            t("PigeonCall",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft4(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonCall.server", "10.1.2.199:2011", "Execute[34796272]")).child(
				                  e("PigeonCall.app", "server", "")))
				      .child(
				            t("PigeonService",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft5(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonService.client", "10.1.7.127:37897", "Execute[34796272]")).child(
				                  e("PigeonService.app", "client", "")))
				      .child(
				            t("PigeonService",
				                  "groupService:groupNoteService_1.0.1:updateNoteDraft7(Integer,Integer,String,String)",
				                  "", 100).child(e("PigeonService.client", "tuangou-web01.nh:37897", "Execute[34796272]"))
				                  .child(e("PigeonService.app", "client", "")))
				      .child(
				            t("PigeonService",
				                  "groupService:groupNoteService_1.0.1:updateNoteD1aft6(Integer,Integer,String,String)",
				                  "", 100).child(
				                  e("PigeonService.client", "cat.qa.dianpingoa.com:37897", "Execute[34796272]")).child(
				                  e("PigeonService.app", "client", "")));

				return t;
			}
		}.build();

		MessageTree tree = new DefaultMessageTree();
		tree.setDomain("cat");
		tree.setHostName("test");
		tree.setIpAddress("10.10.10.1");
		tree.setThreadGroupName("test");
		tree.setThreadId("test");
		tree.setThreadName("test");
		tree.setMessage(message);
		tree.setMessageId("MobileApi-0a01077f-379304-1362256");
		return tree;
	}
}
