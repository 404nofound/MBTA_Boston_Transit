package com.eddy.mbta.json;

import java.util.List;

public class AlertBean {


    /**
     * data : [{"attributes":{"active_period":[{"end":null,"start":"2019-02-01T04:30:00-05:00"}],"banner":null,"cause":"UNKNOWN_CAUSE","created_at":"2019-02-01T14:30:57-05:00","description":"For access to Braintree Station, travel down to the first floor and use the pedestrian crosswalk to enter the station at the street level.\r\n\r\nThis construction is part of the South Shore Parking Garages Improvements and Renovations Project. The pedestrian bridge will be replaced by a new garage lobby featuring elevators and stairwells.\r\n\r\nAffected routes:\r\nRed Line\r\nNorth Quincy-Braintree shuttle\r\n230\r\n236\r\nMiddleborough/Lakeville Line\r\nKingston/Plymouth Line","effect":"STATION_ISSUE","header":"The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore","informed_entity":[{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"70105"},{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"place-brntn"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"place-brntn"}],"lifecycle":"ONGOING","service_effect":"Change at Braintree","severity":1,"short_header":"The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore","timeframe":"ongoing","updated_at":"2019-04-10T06:08:03-04:00","url":"https://www.mbta.com/southshore"},"id":"293625","links":{"self":"/alerts/293625"},"type":"alert"},{"attributes":{"active_period":[{"end":null,"start":"2019-02-01T14:28:18-05:00"}],"banner":null,"cause":"UNKNOWN_CAUSE","created_at":"2019-02-01T14:28:28-05:00","description":"Access to the Kendall/MIT Station will remain open and accessible on the Main Street side of the station throughout this construction.\r\n \r\nThe pedestrian plaza between Carleton Street and Main Street in Kendall Square will be closed due to the construction of an underground garage. \r\n \r\nCustomers are encouraged to utilize alternative paths to/from the station using the paths on Ames Street, Dock Street, or Wadsworth Street.\r\n\r\nAffected routes:\r\nRed Line","effect":"STATION_ISSUE","header":"The pedestrian walkway near Kendall/MIT Station (inbound) is closed until further notice due to MIT construction.","informed_entity":[{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"70071"},{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"place-knncl"},{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"70072"}],"lifecycle":"ONGOING","service_effect":"Change at Kendall/MIT","severity":1,"short_header":"The pedestrian walkway near Kendall/MIT Station (inbound) is closed until further notice due to MIT construction.","timeframe":"ongoing","updated_at":"2019-02-01T14:28:28-05:00","url":"https://courbanize.com/projects/mit-kendall-square/information"},"id":"293624","links":{"self":"/alerts/293624"},"type":"alert"}]
     * jsonapi : {"version":"1.0"}
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) { this.data = data; }

    public static class DataBean {
        /**
         * attributes : {"active_period":[{"end":null,"start":"2019-02-01T04:30:00-05:00"}],"banner":null,"cause":"UNKNOWN_CAUSE","created_at":"2019-02-01T14:30:57-05:00","description":"For access to Braintree Station, travel down to the first floor and use the pedestrian crosswalk to enter the station at the street level.\r\n\r\nThis construction is part of the South Shore Parking Garages Improvements and Renovations Project. The pedestrian bridge will be replaced by a new garage lobby featuring elevators and stairwells.\r\n\r\nAffected routes:\r\nRed Line\r\nNorth Quincy-Braintree shuttle\r\n230\r\n236\r\nMiddleborough/Lakeville Line\r\nKingston/Plymouth Line","effect":"STATION_ISSUE","header":"The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore","informed_entity":[{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"70105"},{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"place-brntn"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"place-brntn"}],"lifecycle":"ONGOING","service_effect":"Change at Braintree","severity":1,"short_header":"The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore","timeframe":"ongoing","updated_at":"2019-04-10T06:08:03-04:00","url":"https://www.mbta.com/southshore"}
         * id : 293625
         * links : {"self":"/alerts/293625"}
         * type : alert
         */

        private AttributesBean attributes;
        private String id;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class AttributesBean {
            /**
             * active_period : [{"end":null,"start":"2019-02-01T04:30:00-05:00"}]
             * banner : null
             * cause : UNKNOWN_CAUSE
             * created_at : 2019-02-01T14:30:57-05:00
             * description : For access to Braintree Station, travel down to the first floor and use the pedestrian crosswalk to enter the station at the street level.

             This construction is part of the South Shore Parking Garages Improvements and Renovations Project. The pedestrian bridge will be replaced by a new garage lobby featuring elevators and stairwells.

             Affected routes:
             Red Line
             North Quincy-Braintree shuttle
             230
             236
             Middleborough/Lakeville Line
             Kingston/Plymouth Line
             * effect : STATION_ISSUE
             * header : The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore
             * informed_entity : [{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"70105"},{"activities":["BOARD"],"route":"Red","route_type":1,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Kingston","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"Braintree"},{"activities":["BOARD"],"route":"CR-Middleborough","route_type":2,"stop":"place-brntn"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"230","route_type":3,"stop":"place-brntn"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"38671"},{"activities":["BOARD"],"route":"236","route_type":3,"stop":"place-brntn"}]
             * lifecycle : ONGOING
             * service_effect : Change at Braintree
             * severity : 1
             * short_header : The pedestrian bridge at the Braintree Parking Garage is permanently closed due to garage renovations. MBTA.com/southshore
             * timeframe : ongoing
             * updated_at : 2019-04-10T06:08:03-04:00
             * url : https://www.mbta.com/southshore
             */

            private String header;
            private String lifecycle;
            private String service_effect;
            private String updated_at;

            public String getHeader() {
                return header;
            }

            public void setHeader(String header) {
                this.header = header;
            }

            public String getLifecycle() {
                return lifecycle;
            }

            public void setLifecycle(String lifecycle) {
                this.lifecycle = lifecycle;
            }

            public String getService_effect() {
                return service_effect;
            }

            public void setService_effect(String service_effect) { this.service_effect = service_effect; }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

        }


    }
}
