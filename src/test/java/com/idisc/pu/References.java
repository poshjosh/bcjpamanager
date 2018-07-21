package com.idisc.pu;

public interface References {
    
  Class [] ENUM_TYPES = {
      sitetype.class, gender.class, country.class, howdidyoufindus.class, emailstatus.class
  };
    
  enum sitetype {
    rss,  web,  timeline,  trend;
    private sitetype() {} 
  } 
  
  enum gender { Male,  Female,  Other;
    private gender() {} 
  }
  
  enum country { ;
      private country() {} 
  }
  
  enum howdidyoufindus { Throughafriendorcolleague,  Fromtheweb, 
    Magazinesorotherprintmedia,  Tvorotherelectronicmedia,  Throughouragent;
    private howdidyoufindus() {} 
  } 
  
  enum emailstatus { unverified,  verified,  bounced,  disabled_or_discontinued, 
    unable_to_relay,  does_not_exist,  could_not_be_delivered_to,  black_listed, 
    verification_attempted_but_status_unknown,  user_opted_out_of_mailinglist, 
    registered_user,  automated_system_email,  restricted,  invalid_format;
    private emailstatus() {}
  }
}
