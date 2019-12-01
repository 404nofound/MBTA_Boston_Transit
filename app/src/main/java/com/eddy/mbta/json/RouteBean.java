package com.eddy.mbta.json;

import java.util.List;

public class RouteBean {

    /**
     * geocoded_waypoints : [{"geocoder_status":"OK","place_id":"ChIJr_AvqcN544kRAVji1vGX5mg","types":["premise"]},{"geocoder_status":"OK","place_id":"ChIJ0UUka8p544kRNM7rY6gxdkY","types":["establishment","light_rail_station","point_of_interest","transit_station"]}]
     * routes : [{"bounds":{"northeast":{"lat":42.3515283,"lng":-71.1246906},"southwest":{"lat":42.3482765,"lng":-71.140588}},"copyrights":"Map data ©2019 Google","legs":[{"distance":{"text":"0.9 mi","value":1496},"duration":{"text":"20 mins","value":1191},"end_address":"Warren Street, Boston, MA 02135, USA","end_location":{"lat":42.348425,"lng":-71.140588},"start_address":"1110 Commonwealth Avenue, Boston, MA 02215, USA","start_location":{"lat":42.3513106,"lng":-71.1246906},"steps":[{"distance":{"text":"125 ft","value":38},"duration":{"text":"1 min","value":26},"end_location":{"lat":42.3514777,"lng":-71.125055},"html_instructions":"Head <b>northwest<\/b> on <b>Fuller St<\/b> toward <b>Commonwealth Avenue<\/b>","polyline":{"points":"uvnaGhpbqLCBOPKTKNHL"},"start_location":{"lat":42.3513106,"lng":-71.1246906},"travel_mode":"WALKING"},{"distance":{"text":"0.3 mi","value":540},"duration":{"text":"7 mins","value":412},"end_location":{"lat":42.3501293,"lng":-71.1310204},"html_instructions":"Turn <b>left<\/b> onto <b>Commonwealth Avenue<\/b>","maneuver":"turn-left","polyline":{"points":"wwnaGrrbqLPVT^T`@NXNXP\\Td@LZRb@HVDTLd@Pr@Hf@Ff@H`A@`@B~@?PARAXAR?^ANKdBCv@AL?J?N?V@P?H@HBN@HBFFTFPLZFPDLKH"},"start_location":{"lat":42.3514777,"lng":-71.125055},"travel_mode":"WALKING"},{"distance":{"text":"0.6 mi","value":918},"duration":{"text":"13 mins","value":753},"end_location":{"lat":42.348425,"lng":-71.140588},"html_instructions":"Take the crosswalk","polyline":{"points":"ionaGzwcqLGDMHOLKHnAdDlAbDBHx@tB`@jAp@dBn@|A`@jABH@FFRDVBPDX@V@J?J@T?PARATARCVG^IZCPOl@Sx@KGEPJFS`AQt@I\\I\\CTEVCX?R?T?N?P?H@JBV@L@TLh@JVHPJRDJIPFNHOBFDFJHLJFD"},"start_location":{"lat":42.3501293,"lng":-71.1310204},"travel_mode":"WALKING"}],"traffic_speed_entry":[],"via_waypoint":[]}],"overview_polyline":{"points":"uvnaGhpbqLk@z@fAfBfAvB`@~@Nl@^xAPnAJbBBpAE`BSzD@dAFl@`@jAL^SN]VKHnAdDpAlDzA`E`BbEd@tARdAHhAApAEj@Qz@S~@Sx@KGEPJFe@vBSz@Il@CrAD~@Bb@X`ATd@DJIPFNHOHN`@Z"},"summary":"Commonwealth Avenue","warnings":["Walking directions are in beta. Use caution \u2013 This route may be missing sidewalks or pedestrian paths."],"waypoint_order":[]}]
     * status : OK
     */

    private List<RoutesBean> routes;

    public List<RoutesBean> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RoutesBean> routes) {
        this.routes = routes;
    }

    public static class RoutesBean {
        /**
         * bounds : {"northeast":{"lat":42.3515283,"lng":-71.1246906},"southwest":{"lat":42.3482765,"lng":-71.140588}}
         * copyrights : Map data ©2019 Google
         * legs : [{"distance":{"text":"0.9 mi","value":1496},"duration":{"text":"20 mins","value":1191},"end_address":"Warren Street, Boston, MA 02135, USA","end_location":{"lat":42.348425,"lng":-71.140588},"start_address":"1110 Commonwealth Avenue, Boston, MA 02215, USA","start_location":{"lat":42.3513106,"lng":-71.1246906},"steps":[{"distance":{"text":"125 ft","value":38},"duration":{"text":"1 min","value":26},"end_location":{"lat":42.3514777,"lng":-71.125055},"html_instructions":"Head <b>northwest<\/b> on <b>Fuller St<\/b> toward <b>Commonwealth Avenue<\/b>","polyline":{"points":"uvnaGhpbqLCBOPKTKNHL"},"start_location":{"lat":42.3513106,"lng":-71.1246906},"travel_mode":"WALKING"},{"distance":{"text":"0.3 mi","value":540},"duration":{"text":"7 mins","value":412},"end_location":{"lat":42.3501293,"lng":-71.1310204},"html_instructions":"Turn <b>left<\/b> onto <b>Commonwealth Avenue<\/b>","maneuver":"turn-left","polyline":{"points":"wwnaGrrbqLPVT^T`@NXNXP\\Td@LZRb@HVDTLd@Pr@Hf@Ff@H`A@`@B~@?PARAXAR?^ANKdBCv@AL?J?N?V@P?H@HBN@HBFFTFPLZFPDLKH"},"start_location":{"lat":42.3514777,"lng":-71.125055},"travel_mode":"WALKING"},{"distance":{"text":"0.6 mi","value":918},"duration":{"text":"13 mins","value":753},"end_location":{"lat":42.348425,"lng":-71.140588},"html_instructions":"Take the crosswalk","polyline":{"points":"ionaGzwcqLGDMHOLKHnAdDlAbDBHx@tB`@jAp@dBn@|A`@jABH@FFRDVBPDX@V@J?J@T?PARATARCVG^IZCPOl@Sx@KGEPJFS`AQt@I\\I\\CTEVCX?R?T?N?P?H@JBV@L@TLh@JVHPJRDJIPFNHOBFDFJHLJFD"},"start_location":{"lat":42.3501293,"lng":-71.1310204},"travel_mode":"WALKING"}],"traffic_speed_entry":[],"via_waypoint":[]}]
         * overview_polyline : {"points":"uvnaGhpbqLk@z@fAfBfAvB`@~@Nl@^xAPnAJbBBpAE`BSzD@dAFl@`@jAL^SN]VKHnAdDpAlDzA`E`BbEd@tARdAHhAApAEj@Qz@S~@Sx@KGEPJFe@vBSz@Il@CrAD~@Bb@X`ATd@DJIPFNHOHN`@Z"}
         * summary : Commonwealth Avenue
         * warnings : ["Walking directions are in beta. Use caution \u2013 This route may be missing sidewalks or pedestrian paths."]
         * waypoint_order : []
         */

        private BoundsBean bounds;
        private OverviewPolylineBean overview_polyline;
        private List<LegsBean> legs;

        public BoundsBean getBounds() {
            return bounds;
        }

        public void setBounds(BoundsBean bounds) {
            this.bounds = bounds;
        }

        public OverviewPolylineBean getOverview_polyline() {
            return overview_polyline;
        }

        public void setOverview_polyline(OverviewPolylineBean overview_polyline) { this.overview_polyline = overview_polyline; }

        public List<LegsBean> getLegs() {
            return legs;
        }

        public void setLegs(List<LegsBean> legs) {
            this.legs = legs;
        }

        public static class BoundsBean {
            /**
             * northeast : {"lat":42.3515283,"lng":-71.1246906}
             * southwest : {"lat":42.3482765,"lng":-71.140588}
             */

            private NortheastBean northeast;
            private SouthwestBean southwest;

            public NortheastBean getNortheast() {
                return northeast;
            }

            public void setNortheast(NortheastBean northeast) {
                this.northeast = northeast;
            }

            public SouthwestBean getSouthwest() {
                return southwest;
            }

            public void setSouthwest(SouthwestBean southwest) {
                this.southwest = southwest;
            }

            public static class NortheastBean {
                /**
                 * lat : 42.3515283
                 * lng : -71.1246906
                 */

                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }

            public static class SouthwestBean {
                /**
                 * lat : 42.3482765
                 * lng : -71.140588
                 */

                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }
        }

        public static class OverviewPolylineBean {
            /**
             * points : uvnaGhpbqLk@z@fAfBfAvB`@~@Nl@^xAPnAJbBBpAE`BSzD@dAFl@`@jAL^SN]VKHnAdDpAlDzA`E`BbEd@tARdAHhAApAEj@Qz@S~@Sx@KGEPJFe@vBSz@Il@CrAD~@Bb@X`ATd@DJIPFNHOHN`@Z
             */

            private String points;

            public String getPoints() {
                return points;
            }

            public void setPoints(String points) { this.points = points; }
        }

        public static class LegsBean {
            /**
             * distance : {"text":"0.9 mi","value":1496}
             * duration : {"text":"20 mins","value":1191}
             * end_address : Warren Street, Boston, MA 02135, USA
             * end_location : {"lat":42.348425,"lng":-71.140588}
             * start_address : 1110 Commonwealth Avenue, Boston, MA 02215, USA
             * start_location : {"lat":42.3513106,"lng":-71.1246906}
             * steps : [{"distance":{"text":"125 ft","value":38},"duration":{"text":"1 min","value":26},"end_location":{"lat":42.3514777,"lng":-71.125055},"html_instructions":"Head <b>northwest<\/b> on <b>Fuller St<\/b> toward <b>Commonwealth Avenue<\/b>","polyline":{"points":"uvnaGhpbqLCBOPKTKNHL"},"start_location":{"lat":42.3513106,"lng":-71.1246906},"travel_mode":"WALKING"},{"distance":{"text":"0.3 mi","value":540},"duration":{"text":"7 mins","value":412},"end_location":{"lat":42.3501293,"lng":-71.1310204},"html_instructions":"Turn <b>left<\/b> onto <b>Commonwealth Avenue<\/b>","maneuver":"turn-left","polyline":{"points":"wwnaGrrbqLPVT^T`@NXNXP\\Td@LZRb@HVDTLd@Pr@Hf@Ff@H`A@`@B~@?PARAXAR?^ANKdBCv@AL?J?N?V@P?H@HBN@HBFFTFPLZFPDLKH"},"start_location":{"lat":42.3514777,"lng":-71.125055},"travel_mode":"WALKING"},{"distance":{"text":"0.6 mi","value":918},"duration":{"text":"13 mins","value":753},"end_location":{"lat":42.348425,"lng":-71.140588},"html_instructions":"Take the crosswalk","polyline":{"points":"ionaGzwcqLGDMHOLKHnAdDlAbDBHx@tB`@jAp@dBn@|A`@jABH@FFRDVBPDX@V@J?J@T?PARATARCVG^IZCPOl@Sx@KGEPJFS`AQt@I\\I\\CTEVCX?R?T?N?P?H@JBV@L@TLh@JVHPJRDJIPFNHOBFDFJHLJFD"},"start_location":{"lat":42.3501293,"lng":-71.1310204},"travel_mode":"WALKING"}]
             * traffic_speed_entry : []
             * via_waypoint : []
             */

            private DistanceBean distance;
            private DurationBean duration;

            public DistanceBean getDistance() {
                return distance;
            }

            public void setDistance(DistanceBean distance) {
                this.distance = distance;
            }

            public DurationBean getDuration() {
                return duration;
            }

            public void setDuration(DurationBean duration) {
                this.duration = duration;
            }

            public static class DistanceBean {
                /**
                 * text : 0.9 mi
                 * value : 1496
                 */

                private String text;
                private int value;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }

            public static class DurationBean {
                /**
                 * text : 20 mins
                 * value : 1191
                 */

                private String text;
                private int value;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }
        }
    }
}
