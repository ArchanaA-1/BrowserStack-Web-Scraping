package utils;

import java.time.Duration;

public interface IConstant {
	public Long implicityWait = Long.valueOf(10);
	public Duration explicitWait = Duration.ofDays(10);
	public String commonDataFilePath = ".//src//test//java//config//configuration.properties";
	public String excelFilepath = "D:\\Basic Softwares\\sts-4.22.1.RELEASE\\LTFS2\\TestData\\Book1.xlsx"; //write
	public String excelFilepath2 = "D:\\Basic Softwares\\sts-4.22.1.RELEASE\\LTFS2\\TestData\\Book2.xlsx"; //read

}
