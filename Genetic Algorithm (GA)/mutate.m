function [ child ] = mutate( input_child, PM )
%MUTATE Summary of this function goes here
%   Detailed explanation goes here
  
    
    count = 1;
    child = input_child;
    while count < 4
        x = rand;
        if x < PM
            if count == 1
                P = (rand * 16) + 2;       %range(2,18)              
                child(1,1) = P;
            elseif count == 2
                I = (rand * 8.37) + 1.05;  %range(1.05,9.42)
                child(1,2) = I;
            else
                D = (rand * 2.11) + 0.26;  %range(0.26,2.37)
                child(1,3) = D;
            end
        end
        count = count + 1;
    end
    
    

end

