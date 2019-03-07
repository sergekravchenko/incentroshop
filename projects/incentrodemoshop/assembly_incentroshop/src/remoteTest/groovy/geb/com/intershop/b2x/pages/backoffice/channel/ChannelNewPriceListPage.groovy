package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage

import java.text.DateFormat
import java.text.SimpleDateFormat

class ChannelNewPriceListPage extends BOLoginPage {
	
    static content =
    {
         createPriceListForm  { $("form", name:"formMask") }
        
         applyButton { $("input", name:"create")}
    }
    
	static at = {
         waitFor(20) { 
			$("td", class: "table_title", 
			        text: contains("New Price List")
			)
		}
	}
    
    def createPriceList(name)
    {        
        DateFormat format = new SimpleDateFormat("MM/dd/YYYY")
        Calendar calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())

        def startDate   = format.format(calendar.getTime())
        calendar.add(Calendar.MONTH, 6)
        def endDate     = format.format(calendar.getTime())
        
        createPriceListForm.$('input', name: 'CreatePriceListForm_DisplayName').value     name
        createPriceListForm.$('input', name: 'CreatePriceListForm_Id').value              name
        createPriceListForm.$('input', name: 'CreatePriceListForm_StartDate').value       startDate
        //createPriceListForm.$('input', name: 'CreatePriceListForm_StartTime').value     startTime
        createPriceListForm.$('input', name: 'CreatePriceListForm_EndDate').value         endDate
        //createPriceListForm.$('input', name: 'CreatePriceListForm_EndTime').value       endTime
        
        applyButton.click()
    }
}


