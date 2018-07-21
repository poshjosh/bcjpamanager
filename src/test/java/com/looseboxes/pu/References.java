package com.looseboxes.pu;


/**
 * @(#)References.java   30-Nov-2014 01:48:59
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <E>
 */
public interface References<E> {
    
    Class [] ENUM_TYPES = {
        country.class, region.class, gender.class, userstatus.class, currency.class,
        howdidyoufindus.class, itemtype.class, productcategory.class, productsubcategory.class,
        productstatus.class, availability.class, paymentmethod.class, orderstatus.class,
        paymentstatus.class, shippingstatus.class
    };
    
    public static enum country{Nigeria}
    public static enum region{
    Abia,AbujaFederalCapitalTerritory("Abuja Federal Capital Territory"),Adamawa,AkwaIbom,Anambra,
    Bauchi,Bayelsa,Benue,Borno,CrossRiver,Delta,Ebonyi,Edo,Ekiti,Enugu,Gombe,Imo,Jigawa,
    Kaduna,Kano,Katsina,Kebbi,Kogi,Kwara,Lagos,Nassarawa,Niger,Ogun,Ondo,Osun,Oyo,Plateau,
    Rivers,Sokoto,Taraba,Yobe,Zamfara;
        private final String label;
        private region(){ label = null; }
        private region(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum gender{Male,Female}
    public static enum userstatus{Unactivated,Activated,Deactivated,Unregistered}
    public static enum currency{NGN,GBP,USD,EUR}
    public static enum howdidyoufindus{Throughafriendorcolleague, Fromtheweb,
    Magazinesorotherprintmedia, Tvorotherelectronicmedia, Throughouragent}
    public static enum itemtype{DataType("Data Type"),Thing,Intangible,Product,JobPosting("Job Posting"),Vehicle;
        private final String label;
        private itemtype(){ label = null; }
        private itemtype(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum productcategory{Fashion}
    public static enum productsubcategory{
        WomensClothing("Women's Clothing"),WomensShoes("Women's Shoes"),WomensAccessories("Women's Accessories"),
        MensClothing("Men's Clothing"),MensShoes("Men's Shoes"),MensAccessories("Men's Accessories"),
        KidsClothing("Kid's Clothing"),KidsShoes("Kid's Shoes"),KidsAccessories("Kid's Accessories"),
        BabysClothing("Baby's Clothing"),BabysShoes("Baby's Shoes"),BabysAccessories("Baby's Accessories"),Other;
        private final String label;
        private productsubcategory(){ label = null; }
        private productsubcategory(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum productstatus{New,Classic,Refurbished,Used}
    public static enum availability{InStock("In Stock"),LimitedAvailability("Limited Availability"),
    OutOfStock("Out of Stock"), SoldOut("Sold Out"),Moved;
        private final String label;
        private availability(){ label = null; }
        private availability(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum paymentmethod{MasterCard("Master Card"),VisaCard("Visa Card"),
    Verve,BankDeposit("Bank Deposit"),CashonDelivery("Cash on Delivery"),BookonHold("Book on Hold");
        private final String label;
        private paymentmethod(){ label = null; }
        private paymentmethod(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum orderstatus{Pending,Ordered}
    public static enum paymentstatus{Pending,ProcessingPayment("Processing Payment"),
    PaymentReceived("Payment Received"), PaymentDeclined("Payment Declined"),PartPaid("Part Paid");
        private final String label;
        private paymentstatus(){ label = null; }
        private paymentstatus(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    public static enum shippingstatus{Pending,PartiallyDispatched("Partially Dispatched"),
    FullyDispatched("Fully Dispatched"),PartiallyArrived("Partially Arrived"),FullyArrived("Fully Arrived"),
    PartiallyReceived("Partially Received"),FullyReceived("Fully Received");
        private final String label;
        private shippingstatus(){ label = null; }
        private shippingstatus(String label) { this.label = label; }
        public final String getLabel() { return label == null ? super.toString() : label; }
    }
    
//    public static enum emailstatus{unverified,verified,bounced,disabled_or_discontinued,
//    unable_to_relay,does_not_exist,could_not_be_delivered_to,black_listed,
//    verification_attempted_but_status_unknown,user_opted_out_of_mailinglist,
//    registered_user,automated_system_email,restricted,invalid_format}
}
