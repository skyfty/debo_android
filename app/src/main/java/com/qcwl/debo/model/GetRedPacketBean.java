package com.qcwl.debo.model;

import java.util.List;

public class GetRedPacketBean {
    private OfferInfoBean offer_info;
    private List<RedPacketInfoBean> get_info;

    public GetRedPacketBean(OfferInfoBean offer_info, List<RedPacketInfoBean> get_info) {
        this.offer_info = offer_info;
        this.get_info = get_info;
    }

    public GetRedPacketBean() {
    }

    public OfferInfoBean getOffer_info() {
        return offer_info;
    }

    public void setOffer_info(OfferInfoBean offer_info) {
        this.offer_info = offer_info;
    }

    public List<RedPacketInfoBean> getGet_info() {
        return get_info;
    }

    public void setGet_info(List<RedPacketInfoBean> get_info) {
        this.get_info = get_info;
    }

    @Override
    public String toString() {
        return "GetRedPacketBean{" +
                "offer_info=" + offer_info +
                ", get_info=" + get_info +
                '}';
    }
}
