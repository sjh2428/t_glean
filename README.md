# t_glean_New_Feature by jihyeok I need merge

Used IDE, SDK : Eclipse Jee Photon(Apache Tomcat v9.0, JRE-10.0.2), Android Studio(Oreo 8.0), Android Emulator

---------------------------------------------------------------------------------------------------------------------

1. Set Workspace '~\t_glean\server'

![1](https://user-images.githubusercontent.com/26241585/46246066-10db8c00-c433-11e8-970d-85f8406572e1.PNG)



---------------------------------------------------------------------------------------------------------------------

2. File(menu) - Open Projects from File System - Set Directory Path - Click 'Finish'

![2](https://user-images.githubusercontent.com/26241585/46246067-10db8c00-c433-11e8-874e-0989c14c166e.PNG)



---------------------------------------------------------------------------------------------------------------------

3. Set 'Server Runtime Environments'

    'Add' Click - Apache Tomcat v9.0 - Set Apache Tomcat Path - Click 'Finish' - Apply and Close
    
![3](https://user-images.githubusercontent.com/26241585/46246068-11742280-c433-11e8-9e9c-fe56af3f6243.PNG)


    
    
---------------------------------------------------------------------------------------------------------------------

4. Set 'Java Build Path'

    Right Click on Project(T_glean_server) - Properties - Java Build Path - Add Library 'JRE', 'Apache Tomcat'
                                                                           (JRE System Library, Server Runtime)
                                                                           
![4](https://user-images.githubusercontent.com/26241585/46246069-11742280-c433-11e8-970d-be48139ee666.PNG)


                                                                           
<div>
<img width=400 height=450 src="https://user-images.githubusercontent.com/26241585/46246288-36b66000-c436-11e8-9519-f7c3ada99b38.PNG">
<img width=400 height=450 src="https://user-images.githubusercontent.com/26241585/46246289-36b66000-c436-11e8-8ca0-f768fef31d7d.PNG">
</div>


---------------------------------------------------------------------------------------------------------------------

5. Set 'Project Facets'

    Right Click on Project(T_glean_server) - Properties - Project Facets - Check Java Version
    
![5](https://user-images.githubusercontent.com/26241585/46246070-11742280-c433-11e8-8ae3-f62d4307b153.PNG)




---------------------------------------------------------------------------------------------------------------------



!!If you see the following message when running the server,

!!create new 'Maven Project' and then please copy and paste the source code into 'src/main/java'.

![6](https://user-images.githubusercontent.com/26241585/46246386-8c3f3c80-c437-11e8-813f-e7accf2b7199.PNG)

