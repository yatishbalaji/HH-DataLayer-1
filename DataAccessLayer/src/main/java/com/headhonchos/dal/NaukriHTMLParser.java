package com.headhonchos.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by richa on 20/8/14.
 */
public class NaukriHTMLParser {
    public static final Logger logger = LoggerFactory.getLogger(NaukriHTMLParser.class);
    URL completeURL = null;
    public String qualification = "", location = "";

    public String parseUrl(String url) {
        String jobPageData = "";
        try {
            if (url.contains("jobsearch.naukri.com"))
                completeURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (completeURL != null)
            jobPageData = processUrl(completeURL);
        return jobPageData;
    }

    public HashMap<String, String> getMap(String naurkiJobPageParsedData) {
        HashMap<String, String> map = new HashMap<String, String>();
        String parsedDataArray[] = naurkiJobPageParsedData.split("\n");
        for (int i = 0; i < parsedDataArray.length; i++) {
            String val = parsedDataArray[i].trim();
            if (val.equals("Job Title:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("job_title", val);
            } else if (val.equals("Experience:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("experience", val);
                if (val.contains("-")) {
                    try {
                        String exprarr[] = val.split("-");
                        map.put("work_experience_min", exprarr[0].trim());
                        if (exprarr[1].contains("Years")) {
                            String lastexp = exprarr[1].split("Years")[0].trim();
                            map.put("work_experience_max", lastexp);
                        } else {
                            map.put("work_experience_max", exprarr[1].trim());
                        }
                    } catch (Exception e) {
                        System.out.println("No Experience exist...");
                    }
                }
            } else if (val.equals("Salary:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("salary", val);
                if (val.contains("-")) {
                    try {
                        String salarr[] = val.split("-");
                        String startsal = salarr[0].split("INR")[1].replaceAll(",", "").trim();
                        try {
                            Float sal = Float.parseFloat(startsal);
                            sal = sal / 100000;
                            map.put("annual_salary_min", sal + "");
                        } catch (Exception e) {
                            System.out.println("Canonot convert " + startsal + " to integer");
                        }

                        if (salarr[1].contains("P")) {
                            String maxsal = salarr[1].split("P")[0].replaceAll(",", "").trim();
                            try {
                                Float sal = Float.parseFloat(maxsal);
                                sal = sal / 100000;
                                map.put("annual_salary_max", sal + "");
                            } catch (Exception e) {
                                System.out.println("Canonot convert " + maxsal + " to integer");
                            }

                        }
                    } catch (Exception e) {
                        System.out.println("No Salary exist...");
                    }
                }
            } else if (val.equals("JD:")) {
                i++;
                //to remove all html tags
                //val=parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();

                //add later changes
                // remove any <script> tags
                val = parsedDataArray[i].replaceAll("(?i)<script.*?</script>", "");
                val = val.replaceAll("(?i)<(?!(/?(p|br|ul|ol|li|b|strong|i|u)))[^>]*>", " ").trim();
                //done changes

                if (val.contains("Read more     ."))
                    val = val.replaceAll("Read more     \\.", "");
                if (val.contains("Click here     ."))
                    val = val.replaceAll("Click here     \\.", "");
                //to remove tag like &nbsp
                val = val.replace("\u00a0", "");
                map.put("job_description", val);
            } else if (val.equals("Industry")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("industry", val);
            } else if (val.equals("Functional Area")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("function", val);
            } else if (val.equals("Role Category")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("sub_function", val);
            } else if (val.equals("Role")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("designation", val);
            } else if (val.equals("Keyskills")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").substring(1).trim();
                map.put("keyskills", val);
            } else if (val.equals("Company Name:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("company_name", val);
            } else if (val.equals("Company Profile:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                map.put("company_profile", val);
            }

            if (val.equals("Education:")) {
                i++;
                val = parsedDataArray[i].trim();
                int index = val.indexOf("<br />");
                if (index == 0)
                    val = val.substring(6);
                setQualification(val);
                String eduarr[] = null;
                if (val.contains("<br />"))
                    eduarr = val.split("<br />");
                if (eduarr != null && eduarr.length > 0) {
                    int k = 0;
                    String edu1 = eduarr[k].replaceAll("\\<.*?\\>", " ").trim();
                    map.put("education1", edu1);
                    if (eduarr.length > 1) {
                        k++;
                        String edu2 = eduarr[k].replaceAll("\\<.*?\\>", " ").trim();
                        map.put("education2", edu2);
                    }
                } else {
                    map.put("education1", val.replaceAll("\\<.*?\\>", " "));
                }

            }

            if (val.equals("Location:")) {
                i++;
                val = parsedDataArray[i].replaceAll("\\<.*?\\>", " ").trim();
                setLocation(val);
                map.put("location", val);
            }

        }
        return map;
    }

    private void setQualification(String qual) {
        String q = "";
        if (qual.contains("<br /><strong>")) {
            String qarr[] = qual.split("<br /><strong>");
            for (int i = 0; i < qarr.length; i++) {
                q = q + qarr[i].replaceAll("\\<.*?\\>", " ").trim().toLowerCase() + "\n";
            }
        } else
            q = qual.replaceAll("\\<.*?\\>", " ").trim().toLowerCase();
        qualification = q;
    }

    public String getQualification() {
        return qualification;
    }

    private void setLocation(String loc) {
        location = loc.toLowerCase();
    }

    public String getLocation() {
        return location;
    }

    /**
     * to extract data from the URL
     *
     * @param completeURL url to be processed
     * @return string of lines containing all data
     */
    private String processUrl(URL completeURL) {

        String line = "";
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(completeURL.openStream()));
        } catch (Exception e) {
            logger.error("Naukri Read Data exception",e);
            return null;
        }

        String cp = null;

        try {
            int flag = 0, edu = 0, jdstart = 0, cname = 0, dcp = 0;
            String jd = "";
            while ((cp = rd.readLine()) != null) {
                if (cp.contains("<article class=\"jdPanel\">"))
                    flag = 1;

                if (flag == 1) {
                    //job title
                    if (cp.contains("<h1 class=\"listHd\">")) {
                        String jt = cp.split("</h1>")[0];
                        int index = jt.lastIndexOf("\"listHd\">");
                        jt = jt.substring(index + 9);
                        line = line + "\nJob Title:\n" + jt.replaceAll("\\<.*?\\>", " ").trim() + "\n";
                    }
                    //experience
                    else if (cp.contains("<em class=\"expIc fl\">")) {
                        String exp = cp.split("</span>")[0];
                        int index = exp.lastIndexOf(">");
                        exp = exp.substring(index + 1);
                        line = line + "\nExperience:\n" + exp.trim() + "\n";
                    }
                    //location
                    else if (cp.contains("<p><em class=\"locIc fl\">")) {
                        String loc = cp.split("</span>")[0];
                        int index = loc.lastIndexOf(">");
                        loc = loc.substring(index + 1);
                        line = line + "\nLocation:\n" + loc.trim() + "\n";
                    }
                    //salary
                    else if (cp.contains("<p><em class=\"rupeeIc  fl\">")) {
                        String sal = cp.split("</span>")[0];
                        int index = sal.lastIndexOf(">");
                        sal = sal.substring(index + 1);
                        line = line + "\nSalary:\n" + sal.trim() + "\n";
                    }
                    //job descryption
                    else if (cp.contains("<div class=\"jobDesc\">")) {
                        jdstart = 1;
                        jd = "";
                    }
                    //industry functional_area role_industry role
                    else if (cp.contains("<div class=\"gridCol\"")) {
                        if (cp.contains("<div class=\"gridCol\" title=\"")) {
                            String detail = cp.split("title=\"")[1].split("\">")[0];
                            line = line + "\n" + detail.trim() + "\n";
                        } else if (cp.contains("</div>")) {
                            String detail = cp.split("</div>")[0].split("\">")[1];
                            line = line + "\n" + detail.trim() + "\n";
                        }
                    } else if (cp.contains("<div class=\"gridCol fade\">")) {
                        String detail = cp.split("</div>")[0];
                        int index = detail.lastIndexOf(">");
                        detail = detail.substring(index + 1);
                        line = line + "\n" + detail.trim();
                        if (detail.equals("Keyskills")) {
                            line = line + "\n";
                            String cpp = rd.readLine();
                            if (cpp.contains("<div class=\"gridCol\"><a")) {
                                String a1[] = cpp.split("<a");
                                for (int k = 1; k < a1.length; k++) {
                                    cpp = a1[k];
                                    String[] kwarr = cpp.split("</a>");
                                    for (int i = 0; i < kwarr.length; i++) {
                                        if (!kwarr[i].trim().equals("") && !kwarr[i].trim().equals("</div>")) {
                                            if (kwarr[i].contains("</em>")) {
                                                String emsk[] = kwarr[i].split("</em>");
                                                for (int j = 0; j < emsk.length; j++) {

                                                    index = emsk[j].lastIndexOf(">");
                                                    String kw = emsk[j].substring(index + 1).replaceAll("\\<.*?\\>", " ");
                                                    line = line + ", " + kw.trim();
                                                }
                                            } else {
                                                index = kwarr[i].lastIndexOf("\">");
                                                String kw = kwarr[i].substring(index + 2).replaceAll("\\<.*?\\>", " ");
                                                line = line + ", " + kw.trim();
                                            }
                                        }
                                    }
                                }

                            } else if (cpp.contains("<div class=\"gridCol\"><em")) {
                                String kwarr[] = cpp.split("</em>");
                                for (int i = 0; i < kwarr.length - 1; i++) {
                                    String kw = kwarr[i];
                                    if (kw.contains("<a")) {
                                        String[] kk = kw.split("</a>");
                                        for (int j = 0; j < kk.length; j++) {
                                            String kw1 = kk[j];
                                            index = kw1.lastIndexOf(">");
                                            kw1 = kw1.substring(index + 1);
                                            line = line + ", " + kw1;
                                        }
                                    } else {
                                        index = kw.lastIndexOf(">");
                                        kw = kw.substring(index + 1);
                                        line = line + ", " + kw;
                                    }
                                }

                            }

                        }
                    }
                    //education
                    else if (cp.contains("<em class=\"eduIc fl\">")) {
                        edu = 1;
                    }
                    //company_profile
                    else if (cp.contains("<div id=\"empMore\"")) {
                        String cprf[] = cp.split("<div");
                        String cn = cprf[0].split("<p>")[1];
                        line = line + "\nCompany Name:\n" + cn.replaceAll("\\<.*?\\>", " ").trim() + "\n";
                        cname = 1;
                        int index = cprf[1].lastIndexOf("class=\"dispN\">");
                        String cf = cprf[1].substring(index + 14);
                        line = line + "\nCompany Profile:\n" + cf.replaceAll("\\<.*?\\>", " ").trim() + "\n";
                    }
                    //if company name line has </article> tag
                    else if (cp.contains("</article>") && cname == 0) {
                        String cn = cp.split("</article>")[0];
                        if (cn != null && !cn.trim().equals("")) {
                            cn = cn.substring(cn.lastIndexOf(">") + 1);
                            line = line + "\nCompany Name:\n" + cn.replaceAll("\\<.*?\\>", " ").trim() + "\n";
                        }
                    } else if (cp.contains("<div class=\"iconCont profileData\""))
                        dcp = 1;
                    if (edu == 1) {
                        if (cp.contains("</div>")) {
                            edu = 0;
                            line = line + "\n\nEducation:\n" + cp.trim() + "\n";
                        }
                    }
                    if (jdstart == 1) {
                        jd = jd + cp.trim() + ". ";
                        if (cp.contains("</div>")) {
                            jdstart = 0;
                            jd = "Job Description:<br /><br />" + jd.split("<div class=\"jobDesc\">")[1];
                            //line = line + "\nJD:\n"+jd.replaceAll("\\<.*?\\>", " ").trim()+"\n";
                            //line = line + "\nJD:\n"+jd.trim()+"\n";
                        }
                    }
                    if (dcp == 1) {
                        if (cp.contains("</div>"))
                            dcp = 0;

                        if (cp.trim().contains("<div")) {
                            try {
                                cp = cp.split("\">")[1].trim();
                            } catch (Exception e) {

                            }
                            jd = jd + "<br /><br />Candidate Profile:<br />" + cp.trim();
                        } else if (cp.trim().contains("</div>")) {
                            try {
                                cp = cp.split("</div>")[0].trim();
                            } catch (Exception e) {

                            }
                            jd = jd + cp.trim();
                        } else {
                            jd = jd + cp.trim();
                        }
                    }
                }//end of outer if
                if (cp.contains("</article>"))
                    flag = 0;
            }//end of while
            line = line + "\n\nJD:\n" + jd.trim() + "\n";
        } catch (IOException e) {
            logger.debug("Error in reading Naukri Input Stream.",e);
        } finally {
            if (rd != null)
                try {
                    rd.close();
                } catch (IOException e) {
                    logger.error("Error closing Naukri Input Stream",e);
                }
        }
        return line;
    }
}
