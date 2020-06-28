package com.mahc.custombottomsheet.Class;

public class RouteList1 {

    String route_number;
    String route_detail;
    String route_dd;

    public RouteList1(String route_number, String route_detail,String route_dd) {
        this.route_number = route_number;
        this.route_detail = route_detail;
        this.route_dd=route_dd;
    }

    public String getRoute_number() {
        return route_number;
    }

    public void setRoute_number(String route_number) {
        this.route_number = route_number;
    }

    public String getRoute_detail() {
        return route_detail;
    }

    public void setRoute_detail(String route_detail) {
        this.route_detail = route_detail;
    }

    public String getRoute_dd() {
        return route_dd;
    }

    public void setRoute_dd(String route_dd) {
        this.route_dd = route_dd;
    }
}

