cd "D:\S4\MrNaina\SuiteFramework\Framework\"
jar cvf framework.jar *
copy "./framework.jar" "D:\S4\MrNaina\SuiteFramework\TestFramework\WEB-INF\lib\"
cd "D:\S4\MrNaina\SuiteFramework\TestFramework\WEB-INF\classes"
javac -cp "D:\S4\MrNaina\SuiteFramework\TestFramework\WEB-INF\lib\framework.jar" -d . *.java
cd "D:\S4\MrNaina\SuiteFramework\TestFramework"
jar cvf "TestFramework.war" *
copy "TestFramework.war" "C:\Program Files\Apache Software Foundation\Tomcat 10.0\webapps\"