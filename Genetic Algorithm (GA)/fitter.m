function [score] = fitter(x)
ISE = x(1,1);
t_r = x(2,1);
t_s = x(3,1);
M_p = x(4,1);

check_nan = [ISE t_r t_s M_p];
checker = isnan(check_nan);

if sum(checker) > 0
  score = 0;
else
  if M_p == 0
      score = (1 / ISE) + (1 / t_r) + (1 / t_s);
  else
      score = (1 / ISE) + (1 / t_r) + (1 / t_s) + (1 / M_p);
  end
  
end
