package com.team.updevic001.mail;

import lombok.Getter;

@Getter
public enum EmailTemplate {

    VERIFICATION("Here's the 6-digit verification code you requested\n",
            """
                    Hi {userName},
                    Use the code below to finish your sign up.
                    {code}
                    This code expires in 5 minutes.
                    Ignore this email if you have not made the request."""
    ),

    PASSWORD_RESET("You have requested to reset your password\n",
            """
                    Hi {userName},
                    Use the code below to reset your password:
                    {code}
                    Ignore this email if you do remember your password, or you have not made the request."""
    ),

    APPLICATION_FORM_INFO_ENG(
            "Your application has been received\n",
            """
                    Hello {userName},
                    
                    You have registered on the Up-devic online course platform to obtain a teacher profile.
                    
                    Once your application has been reviewed, we will inform you about the result via email.
                    
                    Best regards,
                    The Up-devic Team
                    """
    ),

    APPLICATION_FORM_INFO_AZ(
            "Sizin müraciətiniz qeydə alındı\n",
            """
                    Salam {userName} ,
                    
                    Siz Up-devic onlayn kurs platformasında müəllim profili əldə etmək üçün qeydiyyatdan keçmisiniz.
                    
                    Müraciətiniz yoxlanıldıqdan sonra nəticə barədə Sizə əlavə məlumat veriləcək.
                    
                    Hörmətlə,
                    Up-devic komandası
                    """
    ),

    APPLICATION_APPROVED(
            "Your application has been approved\n",
            """
                    Hello {userName},
                    
                    Your application for a teacher profile on the UpDevic platform has been successfully approved.
                    
                    You can now start planning your courses in the field you selected and connect with students.
                    
                    Best regards,
                    The UpDevic Team
                    """
    ),
    APPLICATION_CANCELLED(
            "Your application has not been approved\n",
            """
                    Hello {userName},
                    
                    Unfortunately, your application for a teacher profile on the UpDevic platform has not been approved.
                    
                    Reason for rejection:
                    {cancellationReason}
                    
                    You may reapply in the future after addressing the above reason.
                    
                    Best regards,
                    The UpDevic Team
                    """
    ),


    BALANCE_RESET_INFO("Teachers' balances have been reset",
            """
                    Hi
                    Teachers' balances are reset, please make monthly payments to teachers according to this list.
                    """);


    private final String subject;
    private final String body;


    EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

}