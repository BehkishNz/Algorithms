function [ child ] = crossover( parents, PC )
%CROSSOVER Summary of this function goes here
%   Detailed explanation goes here

  ALPHA = 0.5;
  mom = parents(1, 1:3);
  dad = parents(2, 1:3);
  
  x = rand;
  if x < ALPHA
      child = mom;
  else
      child = dad;
  end
  
  x = rand;
  if x < PC
    x = rand;
    if x <= (1/3)
      % crossover P
      i = 1;
    elseif x > (1/3) && x <= (2/3)
      % crossover I
      i = 2;
    else
      % crossover D
      i = 3;
    end
    
    element_from_mom = mom(1, i);
    element_from_dad = dad(1, i);
    
    child(1, i) = (ALPHA * element_from_mom) + ((1 - ALPHA) * element_from_dad);
  end
end

