package addresshelper.kazumi.bean;

import java.util.LinkedList;

/**
 * Created by KAZUMI on 2017-09-04.
 * ====
 * Gson 解析用 JavaBean.
 */

public class LocationBean {

    public LinkedList<Provices> Provice;

    public class Provices {
        public String Name;
        public String Code;
        public LinkedList<Cities> City;

        public class Cities {
            public String Name;
            public String Code;
            public LinkedList<Regions> Region;

            public class Regions {
                public String Name;
                public String Code;
            }
        }
    }
}
