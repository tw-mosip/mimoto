import React from 'react';

export const renderContent = (content: string | { __html: string }) => {
    if (typeof content === 'object' && '__html' in content) {
        return (
            <span 
                dangerouslySetInnerHTML={{
                    __html: content.__html.replace(
                        /<a\s/g, 
                        '<a class="text-blue-600 hover:text-blue-800 underline font-semibold" '
                    )
                }} 
            />
        );
    }
    return content;
};
